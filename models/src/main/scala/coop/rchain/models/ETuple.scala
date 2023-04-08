package coop.rchain.models

import scala.collection.immutable.BitSet

final case class ETuple(
    ps: Seq[Par] = Seq.empty,
    locallyFree: AlwaysEqual[BitSet] = BitSet.empty,
    connectiveUsed: Boolean = false
) extends RhoType
