package coop.rchain.rholang

import cats.effect.Sync
import coop.rchain.metrics
import coop.rchain.metrics.{Metrics, NoopSpan, Span}
import coop.rchain.rholang.Resources.mkRuntime
import coop.rchain.rholang.performanceTestTools.evalAndMeasTime
import coop.rchain.rholang.syntax._
import coop.rchain.shared.Log
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class LocalNamePerformanceTest extends AnyFlatSpec with Matchers {
  val stepSize            = 400
  val numberOfExperiments = 1

  "append map test with Int" should "be performed" in {
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

    println(s"")
    println(s"Append map with Int test:")
    println(s"numberElements, time(ms)")
    for (i <- 1 to numberOfExperiments) {
      val size   = stepSize * i
      val deploy = makeDeploy(size)
      val m      = evalAndMeasTime(deploy)
      println(s"$size, $m")
    }
  }

  "local append map test with Int" should "be performed" in {
    def makeDeploy(numberElements: Int) =
      s"""
         |new return, loop in {
         |  contract @("local", *loop)(@n, @acc, ret) = {
         |    if (n == 0) ret!(acc)
         |    else @("local", *loop)!(n - 1, acc.set(n, n), *ret)
         |  } |
         |  @("local", *loop)!($numberElements, {}, *return)
         |}
         |""".stripMargin

    println(s"")
    println(s"Local append map with Int test:")
    println(s"numberElements, time(ms)")
    for (i <- 1 to numberOfExperiments) {
      val size   = stepSize * i
      val deploy = makeDeploy(size)
      val m      = evalAndMeasTime(deploy)
      println(s"$size, $m")
    }
  }

  "sum test with Int" should "be performed" in {
    def makeDeploy(numberElements: Int) =
      s"""
         |new return, loop in {
         |  contract loop(@n, @acc, ret) = {
         |    if (n == 0) ret!(acc)
         |    else {loop!(n - 1, acc + n, *ret)}
         |  } |
         |  loop!($numberElements, 0, *return)
         |}
         |""".stripMargin

    println(s"")
    println(s"Sum with Int test")
    println(s"numberElements, time(ms)")
    for (i <- 1 to numberOfExperiments) {
      val size   = stepSize * i
      val deploy = makeDeploy(size)
      val m      = evalAndMeasTime(deploy)
      println(s"$size, $m")
    }
  }
  "local sum test with Int" should "be performed" in {
    def makeDeploy(numberElements: Int) =
      s"""
         |new return, loop in {
         |  contract @("local", *loop)(@n, @acc, ret) = {
         |    if (n == 0) ret!(acc)
         |    else {@("local", loop)!(n - 1, acc + n, *ret)}
         |  } |
         |  @("local", *loop)!($numberElements, 0, *return)
         |}
         |""".stripMargin

    println(s"")
    println(s"Local sum with Int test")
    println(s"numberElements, time(ms)")
    for (i <- 1 to numberOfExperiments) {
      val size   = stepSize * i
      val deploy = makeDeploy(size)
      val m      = evalAndMeasTime(deploy)
      println(s"$size, $m")
    }
  }
}

object performanceTestTools {
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
