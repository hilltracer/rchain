package io.rhonix.models.protobuf

import monix.eval.Coeval

import java.util.Objects
import scala.collection.immutable.BitSet

final case class ParMapProto(
    ps: SortedParMapProto,
    connectiveUsed: Boolean,
    locallyFree: Coeval[BitSet],
    remainder: Option[VarProto]
) {

  override def equals(o: scala.Any): Boolean = o match {
    case parMap: ParMapProto =>
      this.ps == parMap.ps && this.remainder == parMap.remainder && this.connectiveUsed == parMap.connectiveUsed
    case _ => false
  }

  override def hashCode(): Int = Objects.hash(ps, remainder, Boolean.box(connectiveUsed))
}

object ParMapProto {
  def apply(
      seq: Seq[(ParProto, ParProto)],
      connectiveUsed: Boolean,
      locallyFree: Coeval[BitSet],
      remainder: Option[VarProto]
  ) =
    new ParMapProto(SortedParMapProto(seq), connectiveUsed, locallyFree.memoize, remainder)

  def apply(
      seq: Seq[(ParProto, ParProto)],
      connectiveUsed: Boolean,
      locallyFree: BitSet,
      remainder: Option[VarProto]
  ): ParMapProto =
    apply(seq, connectiveUsed, Coeval.pure(locallyFree), remainder)

  def apply(seq: Seq[(ParProto, ParProto)]): ParMapProto =
    apply(seq, connectiveUsed(seq), updateLocallyFree(seq), None)

  def apply(map: SortedParMapProto): ParMapProto =
    apply(map.toSeq)

  private def connectiveUsed(map: Seq[(ParProto, ParProto)]): Boolean =
    map.exists { case (k, v) => k.connectiveUsed || v.connectiveUsed }

  private def updateLocallyFree(ps: Seq[(ParProto, ParProto)]): BitSet =
    ps.foldLeft(BitSet()) {
      case (acc, (key, value)) =>
        acc | key.locallyFree | value.locallyFree
    }

}
