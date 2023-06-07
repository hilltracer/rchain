package coop.rchain.models.rholangN

import cats.Eval
import cats.implicits.toFoldableOps

object ParManager {
  type M[T] = Eval[T]
  @inline private def laterM[T](f: => T): M[T] = Eval.later(f)

  object Constructor {
    import ConnectiveUsed._
    def createParProc(ps: Seq[Par]): ParProc = {
      val meta = new ParMetaData(connectiveUsedParProc(ps))
      new ParProc(ps, meta)
    }

    def createGInt(v: Long): GInt = {
      val meta = new ParMetaData(connectiveUsedGInt(v))
      new GInt(v, meta)
    }
  }

  private object ConnectiveUsed {
    private def cUsedParSeq(ps: Seq[Par]): M[Boolean] =
      ps.foldM[M, Boolean](false) {
        case (acc, x) =>
          x.connectiveUsedM.map { x =>
            println("computation ParProc")
            x || acc
          }
      }
    def connectiveUsedParProc(ps: Seq[Par]): M[Boolean] = cUsedParSeq(ps)
    def connectiveUsedGInt(v: Long): M[Boolean] = laterM {
      println("computation GInt")
      false
    }
  }
}
