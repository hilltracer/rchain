package coop.rchain.models.rholangN

import scodec.bits.ByteVector
import coop.rchain.rspace.hashing.Blake2b256Hash

import scala.collection.immutable.BitSet
import ParManager.Constructor._
import ParManager._

sealed trait Par {
  protected def meta: ParMetaData

  override def equals(x: Any): Boolean = ParManager.equals(this, x)
  override def hashCode: Int           = meta.rhoHash.hashCode()

  def serializedSize: Int         = meta.serializedSize
  def rhoHash: Blake2b256Hash     = meta.rhoHash
  def locallyFree: BitSet         = meta.locallyFree
  def connectiveUsed: Boolean     = meta.connectiveUsed
  def evalRequired: Boolean       = meta.evalRequired
  def substituteRequired: Boolean = meta.substituteRequired

  def toBytes: ByteVector = parToBytes(this)
}
object Par {
  def fromBytes(bytes: ByteVector): Par = parFromBytes(bytes)
}
sealed trait Expr extends Par

class ParProc(val ps: SortedParSeq, protected val meta: ParMetaData) extends Par {
  def add(p: Par): ParProc = ParProc(ps.add(p))
}
object ParProc {
  def apply(ps: Seq[Par]): ParProc           = createParProc(ps)
  def apply(sortedPs: SortedParSeq): ParProc = createParProc(sortedPs)
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
    val data: SortedParSeq,
    val persistent: Boolean,
    protected val meta: ParMetaData
) extends Par
object Send {
  def apply(chan: Par, data: Seq[Par], persistent: Boolean): Send =
    createSend(chan, data, persistent)
}

final class ParMetaData(
    val serializedSize: Int,
    val rhoHash: Blake2b256Hash,
    val locallyFree: BitSet,
    val connectiveUsed: Boolean,
    val evalRequired: Boolean,
    val substituteRequired: Boolean
)

class SortedParSeq(private val sortedData: Seq[Par]) {
  def add(element: Par): SortedParSeq = {
    val index = sortedData.indexWhere(_.rhoHash.bytes >= element.rhoHash.bytes)
    if (index == -1) {
      new SortedParSeq(sortedData :+ element)
    } else {
      new SortedParSeq(sortedData.patch(index, Seq(element), 0))
    }
  }
  def merge(other: SortedParSeq): SortedParSeq =
    new SortedParSeq(
      (sortedData ++ other.sortedData).sorted(Ordering.by((p: Par) => p.rhoHash.bytes))
    )
  def remove(element: Par): SortedParSeq = {
    val index = sortedData.indexWhere(_.rhoHash.bytes == element.rhoHash.bytes)
    if (index != -1) {
      new SortedParSeq(sortedData.patch(index, Nil, 1))
    } else {
      this
    }
  }
  def foreach(f: Par => Unit): Unit = sortedData.foreach(f)
  def size: Int                     = sortedData.size
  def toSeq: Seq[Par]               = sortedData
}

object SortedParSeq {
  // From presorted Seq[Par]
  def fromSorted(SortedParSeq: Seq[Par]): SortedParSeq =
    new SortedParSeq(SortedParSeq)
  // From unsorted Seq[Par]
  def apply(elements: Seq[Par]): SortedParSeq =
    new SortedParSeq(elements.sorted(Ordering.by((p: Par) => p.rhoHash.bytes)))
}
