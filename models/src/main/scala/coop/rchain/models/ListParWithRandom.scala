package coop.rchain.models

import com.google.protobuf.ByteString
import coop.rchain.crypto.hash.Blake2b512Random

final case class ListParWithRandom(
    pars: Seq[Par] = Seq.empty,
    randomState: Blake2b512Random = Blake2b512Random(ByteString.EMPTY.toByteArray)
) extends RhoType
