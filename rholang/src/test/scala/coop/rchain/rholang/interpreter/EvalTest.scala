package coop.rchain.rholang.interpreter

import coop.rchain.metrics
import coop.rchain.metrics.{Metrics, NoopSpan, Span}
import coop.rchain.models.Expr.ExprInstance.GString
import coop.rchain.models.Par
import coop.rchain.models.rholang.implicits._
import coop.rchain.rholang.Resources.mkRuntime
import coop.rchain.rholang.interpreter.errors.{InterpreterError, ReduceError}
import coop.rchain.rholang.syntax._
import coop.rchain.shared.Log
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._
import coop.rchain.rholang.interpreter.compiler.Compiler

class EvalTest extends AnyWordSpec with Matchers {
  implicit val logF: Log[Task]            = Log.log[Task]
  implicit val noopMetrics: Metrics[Task] = new metrics.Metrics.MetricsNOP[Task]
  implicit val noopSpan: Span[Task]       = NoopSpan[Task]()
  private val maxDuration                 = 5.seconds

  val outcomeCh      = "ret"
  val reduceErrorMsg = "Error: index out of bound: -1"

  private def execute(source: String): Task[Par] =
    mkRuntime[Task]("rholang-eval-spec")
      .use { runtime =>
        for {
          _    <- runtime.evaluate(source)
          data <- runtime.getData(GString(outcomeCh)).map(_.head)
        } yield data.a.pars.head
      }

  "runtime" should {
    "convert term to Par and evalue it" in {
//      val term = """{ "key11"|"key12":"data1", "key2":"data2"}"""
      val term = """ new x, y in { x!(*y) | for ( @data <- x) {data} } """
      val ast = Compiler[Task].sourceToADT(term).runSyncUnsafe(maxDuration)
      println("AST:")
      println(ast)

      val term2 = s"""@"$outcomeCh"!($term)"""
      val evalTerm = execute(term2).runSyncUnsafe(maxDuration)
      println("evalTerm:")
      println(evalTerm)

      val term3 = s""" @"chan"!( $term ) | for(@q <- @"chan") { @"$outcomeCh"!(q) } """
      val processedTerm = execute(term3).runSyncUnsafe(maxDuration)
      println("processedTerm:")
      println(processedTerm)

    }
  }
}
