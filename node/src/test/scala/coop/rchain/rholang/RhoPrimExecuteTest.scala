package coop.rchain.rholang

import coop.rchain.metrics
import coop.rchain.metrics.{Metrics, NoopSpan, Span}
import coop.rchain.rholang.Resources.mkRuntime
import coop.rchain.rholang.syntax._
import coop.rchain.shared.Log
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{FlatSpec, Matchers}

class RhoPrimExecuteTest extends FlatSpec with Matchers {
  private val tmpPrefix = "rspace-store-"

  implicit val logF: Log[Task]            = new Log.NOPLog[Task]
  implicit val noopMetrics: Metrics[Task] = new metrics.Metrics.MetricsNOP[Task]
  implicit val noopSpan: Span[Task]       = NoopSpan[Task]()

  it should "execute" in {
    mkRuntime[Task](tmpPrefix)
      .use { runtime =>
        for {
          _ <- runtime.evaluate("""
            @{"loop"}!(100, {}) |
            for( @{x0}, @{x1} <= @{"loop"} ) {
              match (x0 == 0) {
                true => {
                  Nil
                }
                false => {
                  @{"loop"}!((x0 - 1), (x1).set(x0,x0))
                }
              }
            }
            """)
        } yield ()
      }
      .runSyncUnsafe()
  }
}
