package io.rhonix.models

/** *
  * String interpolation
  *
  * `"Hello, ${name}" %% {"name": "Bob"}` denotes `"Hello, Bob"`
  */
final case class EPercentPercent(
    p1: Par = Par(),
    p2: Par = Par()
) extends RhoType
