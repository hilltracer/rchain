package coop.rchain.models

final case class ReceiveBind(
    patterns: Seq[Par] = Seq.empty,
    source: Par = Par(),
    remainder: Option[Var] = None,
    freeCount: Int = 0
) extends RhoType
