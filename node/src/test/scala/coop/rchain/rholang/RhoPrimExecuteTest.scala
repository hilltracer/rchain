package coop.rchain.rholang

import cats.effect.Sync
import coop.rchain.metrics
import coop.rchain.metrics.{Metrics, NoopSpan, Span}
import coop.rchain.models.Expr.ExprInstance.{GInt, GString}
import coop.rchain.models.rholang.implicits._
import coop.rchain.models._
import coop.rchain.rholang.Resources.mkRuntime
import coop.rchain.rholang.syntax._
import coop.rchain.shared.Log
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.annotation.tailrec
import scala.concurrent.duration.{Duration, FiniteDuration}

class RhoPrimExecuteTest extends AnyFlatSpec with Matchers {
  val outcomeCh = "return"

  "sum test" should "be performed" in {
    @tailrec
    def sum(n: Long, x: Long): Long = if (n == 0L) x else sum(n - 1L, x + n)

    def makeDeploy(numberElements: Int) =
      s"""
         |@"loop"!($numberElements, 0) |
         |for( @n, @x <= @"loop" ) {
         |  match (n == 0) {
         |    true  => @"$outcomeCh"!(x)
         |    false => @"loop"!((n - 1), x + n)
         |  }  
         |}  
         |""".stripMargin

    val stepSize = 100
    println(s"numberElements, time(ms)")
    for (i <- 1 to 20) {
      val size         = stepSize * i
      val deploy       = makeDeploy(size)
      val (m, resData) = Tools.evalAndMeasTime(deploy)
      val expectedRes  = sum(size.toLong, 0L)
      val curRes       = resData.getGInt
      curRes should equal(expectedRes)
      println(s"$size, $m")
    }
  }

  "Append in List test" should "be performed" in {
    @tailrec
    def listAppend(n: Long, x: EList): EList =
      if (n == 0L) x
      else listAppend(n - 1L, EList(x.ps :+ (GInt(1): Par)))

    def makeDeploy(numberElements: Int) =
      s"""
         |@"loop"!($numberElements, []) |
         |for( @n, @x <= @"loop" ) {
         |  match (n == 0) {
         |    true  => @"$outcomeCh"!(x)
         |    false => @"loop"!((n - 1), x ++ [1])
         |  }  
         |}  
         |""".stripMargin

    val stepSize = 100
    println(s"numberElements, time(ms)")
    for (i <- 1 to 20) {
      val size         = stepSize * i
      val deploy       = makeDeploy(size)
      val (m, resData) = Tools.evalAndMeasTime(deploy)
      val expectedRes  = listAppend(size.toLong, EList())
      val curRes       = resData.getEListBody
      curRes should equal(expectedRes)
      println(s"$size, $m")
    }
  }

  "Append in Map test" should "be performed" in {
    @tailrec
    def MapAppend(n: Long, x: ParMap): ParMap =
      if (n == 0L) x
      else MapAppend(n - 1L, ParMap(x.ps + (GInt(n): Par, GInt(n): Par)))

    def makeDeploy(numberElements: Int) =
      s"""
         |@"loop"!($numberElements, {}) |
         |for( @n, @x <= @"loop" ) {
         |  match (n == 0) {
         |    true  => @"$outcomeCh"!(x)
         |    false => @"loop"!((n - 1), x.set(n,n))
         |  }  
         |}  
         |""".stripMargin

    val stepSize = 100
    println(s"numberElements, time(ms)")
    for (i <- 1 to 20) {
      val size         = stepSize * i
      val deploy       = makeDeploy(size)
      val (m, resData) = Tools.evalAndMeasTime(deploy)
      val expectedRes  = MapAppend(size.toLong, ParMap(SortedParMap.empty))
      val curRes       = resData.getEMapBody
      curRes should equal(expectedRes)
      println(s"$size, $m")
    }
  }

  object Tools {
    private val tmpPrefix = "reducer-loop-speed-up-test"

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

    def evalAndMeasTime(deploy: String): (Long, Expr) =
      mkRuntime[Task](tmpPrefix)
        .use { runtime =>
          for {
            _                      <- timeF(runtime.evaluate(deploy))
            resAndTime             <- timeF(runtime.evaluate(deploy))
            (evalResult, evalTime) = resAndTime
            _                      = assert(evalResult.errors.isEmpty, "Evaluate error:" + evalResult.errors)
            resData                <- runtime.getData(GString(outcomeCh)).map(x => x.head)
            res                    = resData.a.pars.head.exprs.head
          } yield (evalTime, res)
        }
        .runSyncUnsafe()
  }
}
