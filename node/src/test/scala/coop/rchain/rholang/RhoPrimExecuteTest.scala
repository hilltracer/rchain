package coop.rchain.rholang

import cats.effect.Sync
import coop.rchain.metrics
import coop.rchain.metrics.{Metrics, NoopSpan, Span}
import coop.rchain.rholang.Resources.mkRuntime
import coop.rchain.rholang.Tools.evalAndMeasTime
import coop.rchain.rholang.syntax._
import coop.rchain.shared.Log
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration.{Duration, FiniteDuration}

class RhoPrimExecuteTest extends FlatSpec with Matchers {
  "append map test" should "be performed" in {
    def makeDeploy(numberElements: Int) =
      s"""
         |new return, loop in {
         |  contract loop(@n, @acc, ret) = {
         |    if (n == 0) ret!(acc)
         |    else loop!(n - 1, acc.set(n, n), *ret)
         |  } |
         |  loop!($numberElements, {}, *return)
         |}
         |""".stripMargin

    val stepSize = 100
    println(s"numberElements, time(ms)")
    for (i <- 1 to 5) {
      val size   = stepSize * i
      val deploy = makeDeploy(size)
      val m      = evalAndMeasTime(deploy)
      println(s"$size, $m")
    }
  }
  "sum test" should "be performed" in {
    def makeDeploy(numberElements: Int) =
      s"""
         |
         |new return, loop in {
         |  contract loop(@n, @acc, ret) = {
         |    if (n == 0) ret!(acc)
         |    else loop!(n - 1, acc + n, *ret)
         |  } |
         |  // Loop `100` times with initial value `0`
         |  // Return result on `return` channel
         |  loop!($numberElements, 0, *return)
         |}
         |""".stripMargin

    val stepSize = 100
    println(s"numberElements, time(ms)")
    for (i <- 1 to 5) {
      val size   = stepSize * i
      val deploy = makeDeploy(size)
      val m      = evalAndMeasTime(deploy)
      println(s"$size, $m")
    }
  }
}
object Tools {
  private val tmpPrefix = "rspace-store-"

  implicit val logF: Log[Task]            = new Log.NOPLog[Task]
  implicit val noopMetrics: Metrics[Task] = new metrics.Metrics.MetricsNOP[Task]
  implicit val noopSpan: Span[Task]       = NoopSpan[Task]()

  def durationRaw[A](block: => Task[A]): Task[(A, FiniteDuration)] =
    for {
      t0 <- Sync[Task].delay(System.nanoTime)
      a  <- block
      t1 = System.nanoTime
      m  = Duration.fromNanos(t1 - t0)
    } yield (a, m)

  def timeF[A](block: Task[A]): Task[(A, Long)] =
    durationRaw(block).map(x => (x._1, x._2.toMillis))

  def evalAndMeasTime(deploy: String): Long =
    mkRuntime[Task](tmpPrefix)
      .use { runtime =>
        for {
          _             <- timeF(runtime.evaluate(deploy))
          resAndTime    <- timeF(runtime.evaluate(deploy))
          (_, evalTime) = resAndTime
        } yield evalTime
      }
      .runSyncUnsafe()
}
