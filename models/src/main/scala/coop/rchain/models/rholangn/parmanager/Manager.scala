package coop.rchain.models.rholangn.parmanager

import coop.rchain.models.rholangn._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

object Manager {

  def parToBytes(p: ParN): Array[Byte] = {
    val baos = new ByteArrayOutputStream(p.serializedSize)
    Serialization.serialize(p, baos)
    baos.toByteArray
  }

  def parFromBytes(bv: Array[Byte]): ParN = {
    val bais = new ByteArrayInputStream(bv)
    Serialization.deserialize(bais)
  }

  def equals(self: RhoTypeN, other: Any): Boolean = other match {
    case x: RhoTypeN => x.rhoHash sameElements self.rhoHash
    case _           => false
  }

  def sortPars(ps: Seq[ParN]): Seq[ParN]                  = Sorting.sortPars(ps)
  def sortBinds(bs: Seq[ReceiveBindN]): Seq[ReceiveBindN] = Sorting.sortBinds(bs)
  def sortBindsWithT[T](bs: Seq[(ReceiveBindN, T)]): Seq[(ReceiveBindN, T)] =
    Sorting.sortBindsWithT(bs)
  def sortUris(uris: Seq[String]): Seq[String] = Sorting.sortUris(uris)
  def sortInjections(injections: Map[String, ParN]): Seq[(String, ParN)] =
    Sorting.sortInjections(injections)
  def comparePars(p1: ParN, p2: ParN): Int = Sorting.comparePars(p1, p2)

  /** MetaData */
  def rhoHashFn(p: RhoTypeN): Array[Byte]        = RhoHash.rhoHashFn(p)
  def serializedSizeFn(p: RhoTypeN): Int         = SerializedSize.serializedSizeFn(p)
  def connectiveUsedFn(p: RhoTypeN): Boolean     = ConnectiveUsed.connectiveUsedFn(p)
  def evalRequiredFn(p: RhoTypeN): Boolean       = EvalRequired.evalRequiredFn(p)
  def substituteRequiredFn(p: RhoTypeN): Boolean = SubstituteRequired.substituteRequiredFn(p)
}
