package io.rhonix.models

import scala.collection.immutable.BitSet

/** *
  * A send is written `chan!(data)` or `chan!!(data)` for a persistent send.
  *
  * Upon send, all free variables in data are substituted with their values.
  */
final case class Send(
    chan: Par = Par(),
    data: Seq[Par] = Seq.empty,
    persistent: Boolean = false,
    locallyFree: AlwaysEqual[BitSet] = BitSet.empty,
    connectiveUsed: Boolean = false
) extends RhoType
