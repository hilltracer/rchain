package coop.rchain.models

import scala.collection.immutable.BitSet

final case class EList(
    ps: Seq[Par] = Seq.empty,
    locallyFree: AlwaysEqual[BitSet] = BitSet.empty,
    connectiveUsed: Boolean = false,
    remainder: Option[Var] = None
) extends RhoType
