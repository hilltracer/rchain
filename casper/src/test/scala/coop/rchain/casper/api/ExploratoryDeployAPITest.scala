package coop.rchain.casper.api

import cats.effect.Sync
import cats.syntax.all._
import coop.rchain.blockstorage.blockStore.BlockStore
import com.google.protobuf.ByteString
import coop.rchain.casper.helper.{BlockDagStorageFixture, BlockGenerator, TestNode}
import coop.rchain.casper.util.GenesisBuilder.{buildGenesis, buildGenesisParameters}
import coop.rchain.shared.scalatestcontrib.effectTest
import coop.rchain.casper.engine.Engine
import coop.rchain.casper.helper.BlockGenerator._
import coop.rchain.casper.util.ConstructDeploy.{basicDeployData, sourceDeployNowF}
import coop.rchain.metrics.Metrics
import coop.rchain.shared.{Cell, Log}
import coop.rchain.models._
import coop.rchain.models.Expr.ExprInstance.GString
import coop.rchain.casper.PrettyPrinter
import coop.rchain.casper.batch2.EngineWithCasper
import coop.rchain.models.BlockHash.BlockHash
import monix.eval.Task
import org.scalatest.{EitherValues, FlatSpec, Matchers}
import monix.execution.Scheduler.Implicits.global
import coop.rchain.models.syntax._

class ExploratoryDeployAPITest
    extends FlatSpec
    with Matchers
    with EitherValues
    with BlockGenerator
    with BlockDagStorageFixture {
  implicit val metricsEff = new Metrics.MetricsNOP[Task]
  val genesisParameters   = buildGenesisParameters(bondsFunction = _.zip(List(10L, 10L, 10L)).toMap)
  val genesisContext      = buildGenesis(genesisParameters)

  def exploratoryDeploy(term: String, block: BlockHash)(engineCell: Cell[Task, Engine[Task]])(
      implicit blockStore: BlockStore[Task],
      log: Log[Task]
  ) =
    BlockAPI
      .exploratoryDeploy(term, blockHash = block.toHexString.some)(
        Sync[Task],
        engineCell,
        log,
        blockStore
      )

  /*
   * DAG Looks like this:
   *           b3
   *           |
   *           b2
   *           |
   *           b1
   *           |
   *         genesis
   */
  it should "exploratoryDeploy get data from the read only node" in effectTest {
    TestNode.networkEff(genesisContext, networkSize = 3, withReadOnlySize = 1).use {
      case nodes @ n1 +: n2 +: _ +: readOnly +: Seq() =>
        import readOnly.{blockStore, logEff}
        val engine     = new EngineWithCasper[Task](readOnly.casperEff)
        val storedData = "data"
        for {
          produceDeploys <- (0 until 2).toList.traverse(
                             i =>
                               basicDeployData[Task](
                                 i,
                                 shardId = genesisContext.genesisBlock.shardId
                               )
                           )
          putDataDeploy <- sourceDeployNowF[Task](
                            s"""@"store"!("$storedData")""",
                            shardId = genesisContext.genesisBlock.shardId
                          )
          _  <- n1.propagateBlock(putDataDeploy)(nodes: _*)
          b2 <- n1.propagateBlock(produceDeploys(0))(nodes: _*)
          _  <- n2.propagateBlock(produceDeploys(1))(nodes: _*)

          engineCell <- Cell.mvarCell[Task, Engine[Task]](engine)
          result <- exploratoryDeploy(
                     "new return in { for (@data <- @\"store\") {return!(data)}}",
                     b2.blockHash
                   )(
                     engineCell
                   ).map(_.right.value)
          (par, b) = result
          _        = b.blockHash shouldBe PrettyPrinter.buildStringNoLimit(b2.blockHash)
          _ = par match {
            case Seq(Par(_, _, _, Seq(expr), _, _, _, _, _, _)) =>
              expr match {
                case Expr(GString(data)) => data shouldBe storedData
                case _                   => fail("Could not get data from exploretory api")
              }
          }

        } yield ()
    }
  }

  it should "exploratoryDeploy return error on bonded validator" in effectTest {
    TestNode.networkEff(genesisContext, networkSize = 1).use {
      case nodes @ n1 +: Seq() =>
        import n1.{blockStore, logEff}
        val engine = new EngineWithCasper[Task](n1.casperEff)
        for {
          produceDeploys <- (0 until 1).toList.traverse(
                             i =>
                               basicDeployData[Task](
                                 i,
                                 shardId = genesisContext.genesisBlock.shardId
                               )
                           )
          _ <- n1.propagateBlock(produceDeploys(0))(nodes: _*)

          engineCell <- Cell.mvarCell[Task, Engine[Task]](engine)
          result <- exploratoryDeploy("new return in { return!(1) }", ByteString.EMPTY)(
                     engineCell
                   )
          _ = result.left.value shouldBe "Exploratory deploy can only be executed on read-only RNode."

        } yield ()
    }
  }
}
