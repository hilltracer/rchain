package coop.rchain.models

import com.google.protobuf.ByteString
import coop.rchain.crypto.hash.Blake2b512Random

/** *
  * Rholang code along with the state of a split random number
  * generator for generating new unforgeable names.
  */
final case class ParWithRandom(
    body: Par = Par(),
    randomState: Blake2b512Random = Blake2b512Random(ByteString.EMPTY.toByteArray)
) extends RhoType
