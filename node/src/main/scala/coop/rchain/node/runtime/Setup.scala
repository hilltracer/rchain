package coop.rchain.node.runtime

import cats.Parallel
import cats.effect.concurrent.{Deferred, Ref}
import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import cats.mtl.ApplicativeAsk
import cats.syntax.all._
import coop.rchain.blockstorage.casperbuffer.CasperBufferKeyValueStorage
import coop.rchain.blockstorage.deploy.KeyValueDeployStorage
import coop.rchain.blockstorage.{approvedStore, blockStore}
import coop.rchain.casper._
import coop.rchain.casper.api.BlockReportAPI
import coop.rchain.casper.blocks.BlockProcessor
import coop.rchain.casper.blocks.proposer.{Proposer, ProposerResult}
import coop.rchain.casper.dag.BlockDagKeyValueStorage
import coop.rchain.casper.engine.{BlockRetriever, CasperLaunch, EngineCell, Running}
import coop.rchain.casper.genesis.Genesis
import coop.rchain.casper.protocol.BlockMessage
import coop.rchain.casper.state.instances.{BlockStateManagerImpl, ProposerState}
import coop.rchain.casper.storage.RNodeKeyValueStoreManager
import coop.rchain.casper.storage.RNodeKeyValueStoreManager.legacyRSpacePathPrefix
import coop.rchain.casper.util.comm.{CasperPacketHandler, CommUtil}
import coop.rchain.casper.util.rholang.RuntimeManager
import coop.rchain.comm.discovery.NodeDiscovery
import coop.rchain.comm.rp.Connect.ConnectionsCell
import coop.rchain.comm.rp.RPConf
import coop.rchain.comm.transport.TransportLayer
import coop.rchain.crypto.PrivateKey
import coop.rchain.metrics.{Metrics, Span}
import coop.rchain.models.BlockHash.BlockHash
import coop.rchain.models.Par
import coop.rchain.monix.Monixable
import coop.rchain.node.api.AdminWebApi.AdminWebApiImpl
import coop.rchain.node.api.WebApi.WebApiImpl
import coop.rchain.node.api.{AdminWebApi, WebApi}
import coop.rchain.node.configuration.NodeConf
import coop.rchain.node.diagnostics
import coop.rchain.node.runtime.NodeRuntime._
import coop.rchain.node.state.instances.RNodeStateManagerImpl
import coop.rchain.node.web.ReportingRoutes.ReportingHttpRoutes
import coop.rchain.node.web.{ReportingRoutes, Transaction}
import coop.rchain.p2p.effects.PacketHandler
import coop.rchain.rholang.interpreter.RhoRuntime
import coop.rchain.rspace.state.instances.RSpaceStateManagerImpl
import coop.rchain.rspace.syntax._
import coop.rchain.shared._
import coop.rchain.store.InMemoryStoreManager
import fs2.concurrent.Queue
import monix.execution.Scheduler

import java.nio.file.Files

object Setup {
  def setupNodeProgram[F[_]: Monixable: Concurrent: Parallel: ContextShift: Time: Timer: TransportLayer: LocalEnvironment: Log: EventLog: Metrics: NodeDiscovery](
      rpConnections: ConnectionsCell[F],
      rpConfAsk: ApplicativeAsk[F, RPConf],
      commUtil: CommUtil[F],
      blockRetriever: BlockRetriever[F],
      conf: NodeConf,
      eventPublisher: EventPublisher[F]
  )(implicit mainScheduler: Scheduler): F[
    (
        PacketHandler[F],
        APIServers,
        CasperLoop[F],
        CasperLoop[F],
        EngineInit[F],
        CasperLaunch[F],
        ReportingHttpRoutes[F],
        WebApi[F],
        AdminWebApi[F],
        Option[Proposer[F]],
        Queue[F, (Casper[F], Boolean, Deferred[F, ProposerResult])],
        // TODO move towards having a single node state
        Option[Ref[F, ProposerState[F]]],
        BlockProcessor[F],
        Ref[F, Set[BlockHash]],
        Queue[F, (Casper[F], BlockMessage)],
        Option[ProposeFunction[F]]
    )
  ] =
    for {
      // In memory state for last approved block
      lab <- LastApprovedBlock.of[F]

      span = if (conf.metrics.zipkin)
        diagnostics.effects
          .span(conf.protocolServer.networkId, conf.protocolServer.host.getOrElse("-"))
      else Span.noop[F]

      // RNode key-value store manager / manages LMDB databases
//      oldRSpacePath          = conf.storage.dataDir.resolve(s"$legacyRSpacePathPrefix/history/data.mdb")
//      legacyRSpaceDirSupport <- Sync[F].delay(Files.exists(oldRSpacePath))
//      rnodeStoreManager      <- RNodeKeyValueStoreManager(conf.storage.dataDir, legacyRSpaceDirSupport)
      rnodeStoreManager = InMemoryStoreManager()

      // TODO: Old BlockStore migration message, remove after couple of releases from v0.11.0.
      oldBlockStoreExists = conf.storage.dataDir.resolve("blockstore/storage").toFile.exists
      oldBlockStoreMsg    = s"""
       |Old file-based block storage detected (blockstore/storage). To use this version of RNode please first do the migration.
       |Migration should be done with RNode version v0.10.2. More info can be found in PR:
       |https://github.com/rchain/rchain/pull/3342
      """.stripMargin
      _                   <- new Exception(oldBlockStoreMsg).raiseError.whenA(oldBlockStoreExists)

      // Block storage
      blockStore    <- blockStore.create(rnodeStoreManager)
      approvedStore <- approvedStore.create(rnodeStoreManager)

      // Block DAG storage
      blockDagStorage <- BlockDagKeyValueStorage.create[F](rnodeStoreManager)

      // Casper requesting blocks cache
      casperBufferStorage <- CasperBufferKeyValueStorage.create[F](rnodeStoreManager)

      // Deploy storage
      deployStorage <- KeyValueDeployStorage[F](rnodeStoreManager)

      synchronyConstraintChecker = {
        implicit val bs  = blockStore
        implicit val bds = blockDagStorage
        SynchronyConstraintChecker[F]
      }
      lastFinalizedHeightConstraintChecker = {
        implicit val bs  = blockStore
        implicit val bds = blockDagStorage
        LastFinalizedHeightConstraintChecker[F]
      }

      // Runtime for `rnode eval`
      evalRuntime <- {
        implicit val sp = span
        rnodeStoreManager.evalStores.flatMap(RhoRuntime.createRuntime[F](_, Par()))
      }

      // Runtime manager (play and replay runtimes)
      runtimeManagerWithHistory <- {
        implicit val sp = span
        for {
          rStores    <- rnodeStoreManager.rSpaceStores
          mergeStore <- RuntimeManager.mergeableStore(rnodeStoreManager)
          rm <- RuntimeManager
                 .createWithHistory[F](rStores, mergeStore, Genesis.NonNegativeMergeableTagName)
        } yield rm
      }
      (runtimeManager, historyRepo) = runtimeManagerWithHistory

      // Reporting runtime
      reportingRuntime <- {
        implicit val (bs, as, bd, sp) = (blockStore, approvedStore, blockDagStorage, span)
        if (conf.apiServer.enableReporting) {
          // In reporting replay channels map is not needed
          rnodeStoreManager.rSpaceStores.map(ReportingCasper.rhoReporter(_))
        } else
          ReportingCasper.noop.pure[F]
      }

      // RNodeStateManager
      stateManagers <- {
        for {
          exporter           <- historyRepo.exporter
          importer           <- historyRepo.importer
          rspaceStateManager = RSpaceStateManagerImpl(exporter, importer)
          blockStateManager  = BlockStateManagerImpl(blockStore, blockDagStorage)
          rnodeStateManager  = RNodeStateManagerImpl(rspaceStateManager, blockStateManager)
        } yield (rnodeStateManager, rspaceStateManager)
      }
      (rnodeStateManager, rspaceStateManager) = stateManagers

      // Engine dynamic reference
      engineCell          <- EngineCell.init[F]
      envVars             = EnvVars.envVars[F]
      blockProcessorQueue <- Queue.unbounded[F, (Casper[F], BlockMessage)]
      // block processing state - set of items currently in processing
      blockProcessorStateRef <- Ref.of(Set.empty[BlockHash])
      blockProcessor = {
        implicit val (bs, bd)     = (blockStore, blockDagStorage)
        implicit val (br, cb, cu) = (blockRetriever, casperBufferStorage, commUtil)
        BlockProcessor[F]
      }

      // Load validator private key if specified
      validatorIdentityOpt <- ValidatorIdentity.fromPrivateKeyWithLogging[F](
                               conf.casper.validatorPrivateKey
                             )

      // Proposer instance
      proposer = validatorIdentityOpt.map { validatorIdentity =>
        implicit val (bs, bd, ds) = (blockStore, blockDagStorage, deployStorage)
        implicit val (br, ep)     = (blockRetriever, eventPublisher)
        implicit val (sc, lh)     = (synchronyConstraintChecker, lastFinalizedHeightConstraintChecker)
        implicit val (rm, cu, sp) = (runtimeManager, commUtil, span)
        val dummyDeployerKeyOpt   = conf.dev.deployerPrivateKey
        val dummyDeployerKey      = dummyDeployerKeyOpt.flatMap(Base16.decode(_)).map(PrivateKey(_))

        // TODO make term for dummy deploy configurable
        Proposer[F](validatorIdentity, dummyDeployerKey.map((_, "Nil")))
      }

      // Propose request is a tuple - Casper, async flag and deferred proposer result that will be resolved by proposer
      proposerQueue <- Queue.unbounded[F, (Casper[F], Boolean, Deferred[F, ProposerResult])]

      triggerProposeFOpt: Option[ProposeFunction[F]] = if (proposer.isDefined)
        Some(
          (casper: Casper[F], isAsync: Boolean) =>
            for {
              d <- Deferred[F, ProposerResult]
              _ <- proposerQueue.enqueue1((casper, isAsync, d))
              r <- d.get
            } yield r
        )
      else none[ProposeFunction[F]]

      proposerStateRefOpt <- triggerProposeFOpt.traverse(_ => Ref.of(ProposerState[F]()))

      casperLaunch = {
        implicit val (bs, as, bd, ds)     = (blockStore, approvedStore, blockDagStorage, deployStorage)
        implicit val (br, cb, ep)         = (blockRetriever, casperBufferStorage, eventPublisher)
        implicit val (ec, ev, lb, ra, rc) = (engineCell, envVars, lab, rpConfAsk, rpConnections)
        implicit val (sc, lh)             = (synchronyConstraintChecker, lastFinalizedHeightConstraintChecker)
        implicit val (rm, cu)             = (runtimeManager, commUtil)
        implicit val (rsm, sp)            = (rspaceStateManager, span)
        CasperLaunch.of[F](
          blockProcessorQueue,
          blockProcessorStateRef,
          if (conf.autopropose) triggerProposeFOpt else none[ProposeFunction[F]],
          conf.casper,
          !conf.protocolClient.disableLfs,
          conf.protocolServer.disableStateExporter,
          validatorIdentityOpt
        )
      }
      packetHandler = {
        implicit val ec = engineCell
        CasperPacketHandler[F]
      }
      // Bypass fair dispatcher
      /*packetHandler <- {
        implicit val ec = engineCell
        implicit val rb = requestedBlocks
        implicit val sp = span
        CasperPacketHandler.fairDispatcher[F](
          conf.roundRobinDispatcher.maxPeerQueueSize,
          conf.roundRobinDispatcher.giveUpAfterSkipped,
          conf.roundRobinDispatcher.dropPeerAfterRetries
        )
      }*/
      reportingStore <- ReportStore.store[F](rnodeStoreManager)
      blockReportAPI = {
        implicit val bs = blockStore
        BlockReportAPI[F](reportingRuntime, reportingStore, validatorIdentityOpt)
      }
      apiServers = {
        implicit val (ec, bds, bs, sp) = (engineCell, blockDagStorage, blockStore, span)
        implicit val (sc, lh)          = (synchronyConstraintChecker, lastFinalizedHeightConstraintChecker)
        implicit val (ra, rp)          = (rpConfAsk, rpConnections)
        implicit val rm                = runtimeManager
        val isNodeReadOnly             = conf.casper.validatorPrivateKey.isEmpty

        APIServers.build[F](
          evalRuntime,
          triggerProposeFOpt,
          proposerStateRefOpt,
          conf.apiServer.maxBlocksLimit,
          conf.devMode,
          if (conf.autopropose && conf.dev.deployerPrivateKey.isDefined) triggerProposeFOpt
          else none[ProposeFunction[F]],
          blockReportAPI,
          conf.protocolServer.networkId,
          conf.casper.shardName,
          conf.casper.minPhloPrice,
          isNodeReadOnly
        )
      }
      reportingRoutes = {
        ReportingRoutes.service[F](blockReportAPI)
      }
      casperLoop = {
        implicit val br = blockRetriever
        for {
          engine <- engineCell.read
          // Fetch dependencies from CasperBuffer
          _ <- engine.withCasper(_.fetchDependencies, ().pure[F])
          // Maintain RequestedBlocks for Casper
          _ <- BlockRetriever[F].requestAll(conf.casper.requestedBlocksTimeout)
          _ <- Time[F].sleep(conf.casper.casperLoopInterval)
        } yield ()
      }
      // Broadcast fork choice tips request if current fork choice is more then `forkChoiceStaleThreshold` minutes old.
      // For why - look at updateForkChoiceTipsIfStuck method description.
      updateForkChoiceLoop = {
        implicit val (ec, bs, cu, bds) = (engineCell, blockStore, commUtil, blockDagStorage)
        for {
          _ <- Time[F].sleep(conf.casper.forkChoiceCheckIfStaleInterval)
          _ <- Running.updateForkChoiceTipsIfStuck(conf.casper.forkChoiceStaleThreshold)
        } yield ()
      }
      engineInit = engineCell.read >>= (_.init)
      runtimeCleanup = NodeRuntime.cleanup(
        rnodeStoreManager
      )
      transactionAPI = Transaction[F](
        blockReportAPI,
        Par(unforgeables = Seq(Transaction.transferUnforgeable))
      )
      cacheTransactionAPI <- Transaction.cacheTransactionAPI(transactionAPI, rnodeStoreManager)
      webApi = {
        implicit val (ec, bds, bs, sp) = (engineCell, blockDagStorage, blockStore, span)
        implicit val (ra, rc)          = (rpConfAsk, rpConnections)
        implicit val rm                = runtimeManager
        val isNodeReadOnly             = conf.casper.validatorPrivateKey.isEmpty

        new WebApiImpl[F](
          conf.apiServer.maxBlocksLimit,
          conf.devMode,
          cacheTransactionAPI,
          if (conf.autopropose && conf.dev.deployerPrivateKey.isDefined) triggerProposeFOpt
          else none[ProposeFunction[F]],
          conf.protocolServer.networkId,
          conf.casper.shardName,
          conf.casper.minPhloPrice,
          isNodeReadOnly
        )
      }
      adminWebApi = {
        implicit val (ec, sp) = (engineCell, span)
        implicit val (sc, lh) = (synchronyConstraintChecker, lastFinalizedHeightConstraintChecker)
        new AdminWebApiImpl[F](
          triggerProposeFOpt,
          proposerStateRefOpt
        )
      }
    } yield (
      packetHandler,
      apiServers,
      casperLoop,
      updateForkChoiceLoop,
      engineInit,
      casperLaunch,
      reportingRoutes,
      webApi,
      adminWebApi,
      proposer,
      proposerQueue,
      proposerStateRefOpt,
      blockProcessor,
      blockProcessorStateRef,
      blockProcessorQueue,
      triggerProposeFOpt
    )
}
