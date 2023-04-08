package coop.rchain.models

/** *
  * Cost of the performed operations.
  */
final case class PCost(
    cost: Long = 0L
) extends ProtoConvertible
