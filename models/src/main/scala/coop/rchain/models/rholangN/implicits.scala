package coop.rchain.models.rholangN

object implicits {
  implicit def fromParInst(v: ParInst): Par = Par(v)
}
