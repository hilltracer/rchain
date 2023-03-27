package io.rhonix.models

final case class EAnd(
    p1: Par = Par(),
    p2: Par = Par()
) extends RhoType
