package io.rhonix.models

import scala.collection.immutable.BitSet

final case class EMap(
    kvs: Seq[KeyValuePair] = Seq.empty,
    locallyFree: AlwaysEqual[BitSet] = BitSet.empty,
    connectiveUsed: Boolean = false,
    remainder: Option[Var] = None
) extends RhoType
