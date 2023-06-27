package coop.rchain.models.rholangN

import scodec.bits.ByteVector

final class GNilN() extends GroundN
object GNilN { def apply(): GNilN = new GNilN }

final class GBoolN(val v: Boolean) extends GroundN
object GBoolN { def apply(v: Boolean): GBoolN = new GBoolN(v) }

final class GIntN(val v: Long) extends GroundN
object GIntN { def apply(v: Long): GIntN = new GIntN(v) }

final class GBigIntN(val v: BigInt) extends GroundN
object GBigIntN { def apply(v: BigInt): GBigIntN = new GBigIntN(v) }

final class GStringN(val v: String) extends GroundN
object GStringN { def apply(v: String): GStringN = new GStringN(v) }

final class GByteArrayN(val v: ByteVector) extends GroundN
object GByteArrayN {
  def apply(v: ByteVector): GByteArrayN      = new GByteArrayN(v)
  def apply(bytes: Array[Byte]): GByteArrayN = new GByteArrayN(ByteVector(bytes))
}

final class GUriN(val v: String) extends GroundN
object GUriN { def apply(v: String): GUriN = new GUriN(v) }
