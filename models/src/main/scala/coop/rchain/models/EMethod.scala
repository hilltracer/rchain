package coop.rchain.models

import scala.collection.immutable.BitSet

/** *
  * `target.method(arguments)`
  */
@SerialVersionUID(0L)
final case class EMethod(
    methodName: String = "",
    target: Par = Par(),
    arguments: Seq[Par] = Seq.empty,
    locallyFree: AlwaysEqual[BitSet] = BitSet.empty,
    connectiveUsed: Boolean = false
) extends RhoType
