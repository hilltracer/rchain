package io.rhonix.models

final case class ReceiveBind(
    patterns: Seq[Par] = Seq.empty,
    source: Par = Par(),
    remainder: Option[Var] = None,
    freeCount: Int = 0
) extends RhoType
