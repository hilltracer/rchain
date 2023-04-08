package coop.rchain.models

final case class EMatches(
    target: Par = Par(),
    pattern: Par = Par()
) extends RhoType
