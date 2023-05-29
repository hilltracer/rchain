package coop.rchain.models.rholangN

import scodec.bits.ByteVector
import coop.rchain.rspace.hashing.Blake2b256Hash

import scala.collection.immutable.BitSet
import ParManager.Constructor._
import ParManager._

sealed trait Par {
  protected def meta: ParMetaData

  override def equals(x: Any): Boolean = ParManager.equals(this, x)

  lazy val serializedSize: Int         = meta.serializedSizeFn()
  lazy val rhoHash: Blake2b256Hash     = meta.rhoHashFn()
  lazy val locallyFree: BitSet         = meta.locallyFreeFn()
  lazy val connectiveUsed: Boolean     = meta.connectiveUsedFn()
  lazy val evalRequired: Boolean       = meta.evalRequiredFn()
  lazy val substituteRequired: Boolean = meta.substituteRequiredFn()

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
    val serializedSizeFn: () => Int,
    val rhoHashFn: () => Blake2b256Hash,
    val locallyFreeFn: () => BitSet,
    val connectiveUsedFn: () => Boolean,
    val evalRequiredFn: () => Boolean,
    val substituteRequiredFn: () => Boolean
)
