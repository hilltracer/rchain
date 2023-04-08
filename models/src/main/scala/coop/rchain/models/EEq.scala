package coop.rchain.models

final case class EEq(
    p1: Par = Par(),
    p2: Par = Par()
) extends RhoType
