package coop.rchain.models

final case class BindPattern(
    patterns: Seq[Par] = Seq.empty,
    remainder: Option[Var] = None,
    freeCount: Int = 0
) extends RhoType
