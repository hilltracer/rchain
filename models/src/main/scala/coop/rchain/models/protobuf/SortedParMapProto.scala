package coop.rchain.models.protobuf

final class SortedParMapProto private (ps: Map[ParProto, ParProto])
    extends Iterable[(ParProto, ParProto)] {

  // TODO: Merge `sortedList` and `sortedMap` into one VectorMap once available
  lazy val sortedList: List[(ParProto, ParProto)] = ps.toList

  def empty: SortedParMapProto = SortedParMapProto(Map.empty[ParProto, ParProto])

  def iterator: Iterator[(ParProto, ParProto)] = sortedList.toIterator

  def keys: Iterable[ParProto] = sortedList.map(_._1)

  def values: Iterable[ParProto] = sortedList.map(_._2)

  override def equals(that: Any): Boolean = that match {
    case spm: SortedParMapProto => spm.sortedList == this.sortedList
    case _                      => false
  }

  override def hashCode(): Int = sortedList.hashCode()

}

object SortedParMapProto {
  def apply(map: Map[ParProto, ParProto]): SortedParMapProto = new SortedParMapProto(map)

  def apply(seq: Seq[(ParProto, ParProto)]): SortedParMapProto = SortedParMapProto(seq.toMap)

  def empty: SortedParMapProto = SortedParMapProto(Map.empty[ParProto, ParProto])
}
