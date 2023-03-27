package io.rhonix.models

final case class KeyValuePair(
    key: Par = Par(),
    value: Par = Par()
) extends RhoType
