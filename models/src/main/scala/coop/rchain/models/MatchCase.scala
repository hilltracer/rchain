package coop.rchain.models

final case class MatchCase(
    pattern: Par = Par(),
    source: Par = Par(),
    freeCount: Int = 0
) extends RhoType
