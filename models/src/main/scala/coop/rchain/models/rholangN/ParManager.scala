package coop.rchain.models.rholangN

object ParManager {
  type M[T] = cats.Eval[T]

  object Constructor {
    import ConnectiveUsed._
    def createParProc(ps: Seq[Par]): ParProc = {
      val meta = new ParMetaData(cats.Eval.later(connectiveUsedParProc(ps)))
      new ParProc(ps, meta)
    }

    def createGInt(v: Long): GInt = {
      val meta = new ParMetaData(cats.Eval.later(connectiveUsedGInt(v)))
      new GInt(v, meta)
    }
  }

  private object ConnectiveUsed {
    private def cUsedParSeq(ps: Seq[Par])            = ps.exists(_.connectiveUsed)
    def connectiveUsedParProc(ps: Seq[Par]): Boolean = cUsedParSeq(ps)
    def connectiveUsedGInt(v: Long): Boolean         = false
  }
}
