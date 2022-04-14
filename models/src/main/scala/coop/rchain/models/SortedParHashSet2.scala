package coop.rchain.models

import coop.rchain.models.rholang.sorter.Sortable
import coop.rchain.models.rholang.sorter.ordering._
import monix.eval.Coeval

import scala.collection.GenSet

//Enforce ordering and uniqueness.
// - uniqueness is handled by using HashSet.
// - ordering comes from sorting the elements prior to serializing.
final class SortedParHashSet2 private (ps: List[Par]) extends Iterable[Par] {

  lazy val sortedPars: List[Par] = ps.sort

  private lazy val keysMap = sortedPars.map(x => (x.hashCode(), x)).toMap

  //  private lazy val sortedPs: HashSet[Par] = HashSet(sortedPars: _*)

  def +(elem: Par): SortedParHashSet2 = {
    val p          = sort(elem)
    val kCode      = p.hashCode()
    val newKeysMap = keysMap + ((kCode, p))
    SortedParHashSet2(newKeysMap.values.toList)
  }

  def -(elem: Par): SortedParHashSet2 = {
    val p          = sort(elem)
    val kCode      = p.hashCode()
    val newKeysMap = keysMap - kCode
    SortedParHashSet2(newKeysMap.values.toList)
  }

  def contains(elem: Par): Boolean = {
    val p     = sort(elem)
    val kCode = p.hashCode()
    keysMap.contains(kCode)
  }

  def union(that: GenSet[Par]): SortedParHashSet2 = {
    val others     = that.map(x => (x.hashCode(), x)).toMap
    val newKeysMap = keysMap ++ others
    SortedParHashSet2(newKeysMap.values.toList)
  }

  def empty: SortedParHashSet2 = SortedParHashSet2(List.empty[Par])

  def iterator: Iterator[Par] = sortedPars.toIterator

  override def equals(that: Any): Boolean = that match {
    case sph: SortedParHashSet2 => sph.keysMap.keySet == this.keysMap.keySet
    case _                      => false
  }

  override def hashCode(): Int = keysMap.hashCode()

  private def sort(par: Par): Par = Sortable[Par].sortMatch[Coeval](par).map(_.term).value()
}

object SortedParHashSet2 {
  //  def apply(seq: Seq[Par]): SortedParHashSet = new SortedParHashSet(HashSet[Par](seq: _*))
  def apply(seq: Seq[Par]): SortedParHashSet2 = new SortedParHashSet2(seq.toList)

  def apply(set: Set[Par]): SortedParHashSet2 = SortedParHashSet2(set.toSeq)

  def empty: SortedParHashSet2 = SortedParHashSet2(List.empty[Par])
}
