package coop.rchain.models

import scala.collection.immutable.BitSet

final case class Match(
    target: Par = Par(),
    cases: Seq[MatchCase] = Seq.empty,
    locallyFree: AlwaysEqual[BitSet] = BitSet.empty,
    connectiveUsed: Boolean = false
) extends RhoType
