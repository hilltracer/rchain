package coop.rchain.models

final case class EDiv(
    p1: Par = Par(),
    p2: Par = Par()
) extends RhoType
