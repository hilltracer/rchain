package coop.rchain.models.rholangN

import scodec.bits.ByteVector
import coop.rchain.rspace.hashing.Blake2b256Hash

import scala.collection.immutable.BitSet
import ParManager.Constructor._
import ParManager._

sealed trait Par {
  protected def meta: ParMetaData

  override def equals(x: Any): Boolean = ParManager.equals(this, x)

  lazy val serializedSize: Int         = meta.serializedSize.value
  lazy val rhoHash: Blake2b256Hash     = meta.rhoHash.value
  lazy val locallyFree: BitSet         = meta.locallyFree.value
  lazy val connectiveUsed: Boolean     = meta.connectiveUsed.value
  lazy val evalRequired: Boolean       = meta.evalRequired.value
  lazy val substituteRequired: Boolean = meta.substituteRequired.value

  def toBytes: ByteVector = parToBytes(this)
}
object Par {
  def fromBytes(bytes: ByteVector): Par = parFromBytes(bytes)
}
sealed trait Expr extends Par

class ParProc(val ps: Seq[Par], protected val meta: ParMetaData) extends Par {
  def add(p: Par): ParProc = ParProc(ps :+ p)
}
object ParProc {
  def apply(ps: Seq[Par]): ParProc = createParProc(ps)
}

final class GNil(protected val meta: ParMetaData) extends Par
object GNil { def apply(): GNil = createGNil }

final class GInt(val v: Long, protected val meta: ParMetaData) extends Expr
object GInt { def apply(v: Long): GInt = createGInt(v) }

final class EList(val ps: Seq[Par], protected val meta: ParMetaData) extends Expr
object EList {
  def apply(): EList             = createEList(Seq())
  def apply(p: Par): EList       = createEList(Seq(p))
  def apply(ps: Seq[Par]): EList = createEList(ps)
}

final class Send(
    val chan: Par,
    val data: Seq[Par],
    val persistent: Boolean,
    protected val meta: ParMetaData
) extends Par
object Send {
  def apply(chan: Par, data: Seq[Par], persistent: Boolean): Send =
    createSend(chan, data, persistent)
}

final class ParMetaData(
    serializedSizeFn: M[Int],
    rhoHashFn: M[Blake2b256Hash],
    locallyFreeFn: M[BitSet],
    connectiveUsedFn: M[Boolean],
    evalRequiredFn: M[Boolean],
    substituteRequiredFn: M[Boolean]
) {
  lazy val serializedSize: M[Int]         = serializedSizeFn
  lazy val rhoHash: M[Blake2b256Hash]     = rhoHashFn
  lazy val locallyFree: M[BitSet]         = locallyFreeFn
  lazy val connectiveUsed: M[Boolean]     = connectiveUsedFn
  lazy val evalRequired: M[Boolean]       = evalRequiredFn
  lazy val substituteRequired: M[Boolean] = substituteRequiredFn
}
