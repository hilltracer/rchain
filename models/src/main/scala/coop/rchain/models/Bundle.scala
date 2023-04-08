package coop.rchain.models

/** *
  * Nothing can be received from a (quoted) bundle with `readFlag = false`.
  * Likeise nothing can be sent to a (quoted) bundle with `writeFlag = false`.
  *
  * If both flags are set to false, bundle allows only for equivalance check.
  *
  * @param writeFlag
  *   flag indicating whether bundle is writeable
  * @param readFlag
  *   flag indicating whether bundle is readable
  */
final case class Bundle(
    body: Par = Par(),
    writeFlag: Boolean = false,
    readFlag: Boolean = false
) extends RhoType
