package coop.rchain.casper.engine

import cats.Parallel
import cats.effect.concurrent.Deferred
import cats.effect.{Concurrent, ContextShift, Timer}
import cats.syntax.all._
import coop.rchain.blockstorage.BlockStore
import coop.rchain.blockstorage.BlockStore.BlockStore
import coop.rchain.blockstorage.approvedStore.ApprovedStore
import coop.rchain.blockstorage.dag.BlockDagStorage
import coop.rchain.casper.LastApprovedBlock.LastApprovedBlock
import coop.rchain.casper._
import coop.rchain.casper.genesis.Genesis
import coop.rchain.casper.genesis.contracts.{ProofOfStake, Registry, Validator}
import coop.rchain.casper.protocol._
import coop.rchain.casper.rholang.RuntimeManager
import coop.rchain.casper.syntax._
import coop.rchain.casper.util.{BondsParser, VaultParser}
import coop.rchain.comm.PeerNode
import coop.rchain.comm.rp.Connect.{ConnectionsCell, RPConfAsk}
import coop.rchain.comm.transport.TransportLayer
import coop.rchain.metrics.{Metrics, Span}
import coop.rchain.rspace.state.RSpaceStateManager
import coop.rchain.shared._
import coop.rchain.shared.syntax._
import fs2.Stream
import fs2.concurrent.Queue

final case class PeerMessage(peer: PeerNode, message: CasperMessage)

object NodeLaunch {

  // format: off
  def apply[F[_]
    /* Execution */   : Concurrent: Parallel: ContextShift: Time: Timer
    /* Transport */   : TransportLayer: CommUtil: BlockRetriever: EventPublisher
    /* State */       : RPConfAsk: ConnectionsCell: LastApprovedBlock
    /* Rholang */     : RuntimeManager
    /* Casper */      : LastFinalizedHeightConstraintChecker: SynchronyConstraintChecker
    /* Storage */     : BlockStore: ApprovedStore: BlockDagStorage: RSpaceStateManager
    /* Diagnostics */ : Log: EventLog: Metrics: Span] // format: on
  (
      packets: Stream[F, PeerMessage],
      incomingBlocksQueue: Queue[F, BlockMessage],
      conf: CasperConf,
      trimState: Boolean,
      disableStateExporter: Boolean,
      validatorIdentityOpt: Option[ValidatorIdentity],
      casperShardConf: CasperShardConf,
      standalone: Boolean
  ): F[Unit] = {

    def createStoreBroadcastGenesis: F[Unit] =
      for {
        _ <- Log[F]
              .warn(
                "Public key for PoS vault is not set, manual transfer from PoS vault will be disabled (config 'pos-vault-pub-key')"
              )
              .whenA(conf.genesisBlockData.posVaultPubKey.isEmpty)
        _ <- Log[F]
              .warn(
                "Public key for system - contract is not set (config 'system-contract-pub-key')"
              )
              .whenA(conf.genesisBlockData.systemContractPubKey.isEmpty)
        genesisBlock <- createGenesisBlockFromConfig(conf)
        genBlockStr  = PrettyPrinter.buildString(genesisBlock)
        _            <- Log[F].info(s"Sending ApprovedBlock $genBlockStr to peers...")

        // Store genesis block
        _  <- BlockStore[F].put(genesisBlock)
        ab = ApprovedBlock(genesisBlock)
        _  <- ApprovedStore[F].putApprovedBlock(ab)
        _  <- LastApprovedBlock[F].set(ab)
        _  <- BlockDagStorage[F].insert(genesisBlock, invalid = false, approved = true)

        // Send approved block to peers
        _ <- CommUtil[F].streamToPeers(ab.toProto)
      } yield ()

    def startSyncingMode: F[Unit] =
      for {
        dag <- BlockDagStorage[F].getRepresentation
        _ <- new RuntimeException(
              """DAG storage has already stored blocks.
            | LFS syncing is not possible.
            | Please remove old data files from RNode folder.""".stripMargin
            ).raiseError.unlessA(dag.dagSet.isEmpty)
        validatorId <- ValidatorIdentity.fromPrivateKeyWithLogging[F](conf.validatorPrivateKey)
        finished    <- Deferred[F, Unit]
        engine <- NodeSyncing[F](
                   finished,
                   incomingBlocksQueue,
                   casperShardConf,
                   validatorId,
                   trimState
                 )
        handleMessages = packets.parEvalMapUnorderedProcBounded { pm =>
          engine.handle(pm.peer, pm.message)
        }
        _ <- CommUtil[F].requestApprovedBlock(trimState)
        _ <- (Stream.eval(finished.get) concurrently handleMessages).compile.drain
      } yield ()

    def startRunningMode: F[Unit] =
      for {
        engine <- NodeRunning[F](
                   incomingBlocksQueue,
                   validatorIdentityOpt,
                   disableStateExporter
                 )
        handleMessages = packets.parEvalMapUnorderedProcBounded { pm =>
          engine.handle(pm.peer, pm.message)
        }
        _ <- Log[F].info(s"Making a transition to Running state.")
        _ <- CommUtil[F].sendForkChoiceTipRequest
        _ <- handleMessages.compile.drain
      } yield ()

    for {
      dag <- BlockDagStorage[F].getRepresentation
      _ <- if (dag.dagSet.isEmpty && standalone) {
            // Create genesis block and send to peers
            Log[F].info("Starting as genesis master, creating genesis block...") *>
              createStoreBroadcastGenesis
          } else if (dag.dagSet.isEmpty) {
            // If state is empty, transition to syncing mode (LFS)
            Log[F].info("Starting from bootstrap node, syncing LFS...") *>
              startSyncingMode
          } else {
            Log[F].info("Reconnecting to existing network...")
          }

      // Transition to running mode
      _ <- startRunningMode
    } yield ()
  }

  def createGenesisBlockFromConfig[F[_]: Concurrent: ContextShift: Time: RuntimeManager: Log](
      conf: CasperConf
  ): F[BlockMessage] =
    createGenesisBlock[F](
      conf.shardName,
      conf.genesisBlockData.genesisBlockNumber,
      conf.genesisBlockData.bondsFile,
      conf.autogenShardSize,
      conf.genesisBlockData.walletsFile,
      conf.genesisBlockData.bondMinimum,
      conf.genesisBlockData.bondMaximum,
      conf.genesisBlockData.epochLength,
      conf.genesisBlockData.quarantineLength,
      conf.genesisBlockData.numberOfActiveValidators,
      conf.genesisBlockData.posMultiSigPublicKeys,
      conf.genesisBlockData.posMultiSigQuorum,
      conf.genesisBlockData.posVaultPubKey,
      conf.genesisBlockData.systemContractPubKey
    )

  def createGenesisBlock[F[_]: Concurrent: ContextShift: Time: RuntimeManager: Log](
      shardId: String,
      blockNumber: Long,
      bondsPath: String,
      autogenShardSize: Int,
      vaultsPath: String,
      minimumBond: Long,
      maximumBond: Long,
      epochLength: Int,
      quarantineLength: Int,
      numberOfActiveValidators: Int,
      posMultiSigPublicKeys: List[String],
      posMultiSigQuorum: Int,
      posVaultPubKey: String,
      systemContractPubkey: String
  ): F[BlockMessage] =
    for {
      blockTimestamp <- Time[F].currentMillis

      // Initial REV vaults
      vaults <- VaultParser.parse[F](vaultsPath)

      // Initial validators
      bonds      <- BondsParser.parse[F](bondsPath, autogenShardSize)
      validators = bonds.toSeq.map(Validator.tupled)

      // Run genesis deploys and create block
      genesisBlock <- Genesis.createGenesisBlock(
                       Genesis(
                         shardId = shardId,
                         blockTimestamp = blockTimestamp,
                         proofOfStake = ProofOfStake(
                           minimumBond = minimumBond,
                           maximumBond = maximumBond,
                           epochLength = epochLength,
                           quarantineLength = quarantineLength,
                           numberOfActiveValidators = numberOfActiveValidators,
                           validators = validators,
                           posMultiSigPublicKeys = posMultiSigPublicKeys,
                           posMultiSigQuorum = posMultiSigQuorum,
                           posVaultPubKey = posVaultPubKey
                         ),
                         registry = Registry(systemContractPubkey),
                         vaults = vaults,
                         blockNumber = blockNumber
                       )
                     )
    } yield genesisBlock
}
