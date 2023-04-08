package coop.rchain.models

import scala.collection.immutable.BitSet

/** *
  * A receive is written `for(binds) { body }`
  * i.e. `for(patterns &lt;- source) { body }`
  * or for a persistent recieve: `for(patterns &lt;= source) { body }`.
  *
  * It's an error for free Variable to occur more than once in a pattern.
  */
final case class Receive(
    binds: Seq[ReceiveBind] = Seq.empty,
    body: Par = Par(),
    persistent: Boolean = false,
    peek: Boolean = false,
    bindCount: Int = 0,
    locallyFree: AlwaysEqual[BitSet] = BitSet.empty,
    connectiveUsed: Boolean = false
) extends RhoType
