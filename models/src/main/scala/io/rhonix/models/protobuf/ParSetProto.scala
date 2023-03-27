package io.rhonix.models.protobuf

import monix.eval.Coeval

import java.util.Objects
import scala.collection.immutable.BitSet

//locallyFree is of type Coeval to make use of memoization
final case class ParSetProto(
    ps: SortedParHashSetProto,
    connectiveUsed: Boolean,
    locallyFree: Coeval[BitSet],
    remainder: Option[VarProto]
) {

  override def equals(o: scala.Any): Boolean = o match {
    case parSet: ParSetProto =>
      this.ps == parSet.ps &&
        this.remainder == parSet.remainder &&
        this.connectiveUsed == parSet.connectiveUsed
    case _ => false
  }

  override def hashCode(): Int = Objects.hash(ps, remainder, Boolean.box(connectiveUsed))
}

object ParSetProto {
  def apply(
      ps: Seq[ParProto],
      connectiveUsed: Boolean,
      locallyFree: Coeval[BitSet],
      remainder: Option[VarProto]
  ): ParSetProto =
    ParSetProto(SortedParHashSetProto(ps), connectiveUsed, locallyFree.memoize, remainder)

  def apply(
      ps: Seq[ParProto],
      remainder: Option[VarProto]
  ): ParSetProto = {
    val shs = SortedParHashSetProto(ps)
    ParSetProto(
      shs,
      connectiveUsed(ps) || remainder.isDefined,
      Coeval.delay(updateLocallyFree(shs)).memoize,
      remainder
    )
  }

  def apply(ps: Seq[ParProto]): ParSetProto =
    apply(ps, None)

  private def connectiveUsed(seq: Seq[ParProto]): Boolean =
    seq.exists(_.connectiveUsed)

  private def updateLocallyFree(ps: SortedParHashSetProto): BitSet =
    ps.sortedPars.foldLeft(BitSet())((acc, p) => acc | p.locallyFree)
}
