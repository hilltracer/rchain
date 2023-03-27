package io.rhonix.models.protobuf

import scala.collection.immutable.HashSet

//Enforce ordering and uniqueness.
// - uniqueness is handled by using HashSet.
// - ordering comes from sorting the elements prior to serializing.
final class SortedParHashSetProto(ps: HashSet[ParProto]) extends Iterable[ParProto] {
  lazy val sortedPars: List[ParProto] = ps.toList
  def empty: SortedParHashSetProto    = SortedParHashSetProto(HashSet.empty[ParProto])
  def iterator: Iterator[ParProto]    = sortedPars.toIterator
  override def equals(that: Any): Boolean = that match {
    case sph: SortedParHashSetProto => sph.sortedPars == this.sortedPars
    case _                          => false
  }
  override def hashCode(): Int = sortedPars.hashCode()
}

object SortedParHashSetProto {
  def apply(seq: Seq[ParProto]): SortedParHashSetProto =
    new SortedParHashSetProto(HashSet[ParProto](seq: _*))

  def apply(set: Set[ParProto]): SortedParHashSetProto = SortedParHashSetProto(set.toSeq)

  def empty: SortedParHashSetProto = SortedParHashSetProto(HashSet.empty[ParProto])
}
