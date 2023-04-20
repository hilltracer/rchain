package coop.rchain.models.rholangN

import scodec.bits.ByteVector

import scala.collection.immutable.BitSet

sealed trait ParInst {
  def serializedSize: Int
  def rhoHash: ByteVector
}
sealed trait Expr extends ParInst

final case class Par(ps: Seq[ParInst], opt: ParOptData) extends ParInst {
  override def equals(x: Any): Boolean = PM.equals(this, x)
  override def hashCode: Int         = opt.javaHash
  override def serializedSize: Int = opt.serializedSize
  override def rhoHash: ByteVector = opt.rhoHash

  def toBytes: ByteVector = PM.parToBytes(this)
}

object Par {
  def apply(ps: Seq[ParInst]): Par      = PM.createPar(ps)
  def apply(p: ParInst): Par            = PM.createPar(Seq(p))
  def apply(): Par                      = PM.createPar(Seq())

  def fromBytes(bytes: ByteVector): Par = PM.parFromBytes(bytes)
}

final case class Send(chan: Par, data: Seq[Par], persistent: Boolean, opt: ParOptData)
    extends ParInst {
  override def equals(x: Any): Boolean = PM.equals(this, x)
  override def hashCode: Int         = opt.javaHash
  override def serializedSize: Int = opt.serializedSize
  override def rhoHash: ByteVector = opt.rhoHash
}
object Send {
  def apply(chan: Par, data: Seq[Par], persistent: Boolean): GInt =
    PM.createSend(chan, data, persistent)
}

final case class GInt(v: Long, opt: ParOptData) extends Expr {
  override def equals(x: Any): Boolean = PM.equals(this, x)
  override def hashCode: Int         = opt.javaHash
  override def serializedSize: Int = opt.serializedSize
  override def rhoHash: ByteVector = opt.rhoHash
}
object GInt {
  def apply(v: Long): GInt = PM.createGInt(v)
}

final case class EList(ps: Seq[Par], opt: ParOptData) extends Expr {
  override def equals(x: Any): Boolean = PM.equals(this, x)
  override def hashCode: Int         = opt.javaHash
  override def serializedSize: Int = opt.serializedSize
  override def rhoHash: ByteVector = opt.rhoHash
}
object EList {
  def apply(ps: Seq[Par]): EList = PM.createEList(ps)
  def apply(): EList = PM.createEList(Seq())
}

final case class ParOptData(
    serializedSize: Int,
    rhoHash: ByteVector,
    javaHash: Int,
    locallyFree: BitSet,
    connectiveUsed: Boolean
)