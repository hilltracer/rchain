package coop.rchain.models

import coop.rchain.models.rholang.sorter.Sortable
import coop.rchain.models.rholang.sorter.ordering._
import monix.eval.Coeval

import scala.collection.GenTraversableOnce

final class SortedParMap2 private (ps: List[(Par, Par)]) extends Iterable[(Par, Par)] {

  // TODO: Merge `sortedList` and `sortedMap` into one VectorMap once available
  lazy val sortedList: List[(Par, Par)] = ps.sort

  //  private lazy val sortedMap: HashMap[Par, Par] = HashMap(sortedList: _*)

  private lazy val keyList = sortedList.map(_._1).map(x => (x.hashCode(), x))

  private lazy val keyMap: Map[Int, Par] = Map(keyList: _*)

  private lazy val sortedMapInt: Map[Int, Par] = Map(
    sortedList.map(x => (x._1.hashCode(), x._2)): _*
  )

  def +(kv: (Par, Par)): SortedParMap2 = {
    val (k, v)          = kv
    val kCode           = k.hashCode()
    val newKeyMap       = keyMap + ((kCode, k))
    val newSortedMapInt = sortedMapInt + ((kCode, v))
    val newSortedList = newSortedMapInt.toList.map {
      case (x, y) => (newKeyMap(x), y)
    }
    SortedParMap2(newSortedList)
  }

  def ++(kvs: GenTraversableOnce[(Par, Par)]): SortedParMap2 =
    SortedParMap2(ps ++ kvs)

  def -(key: Par): SortedParMap2 = {
    val kCode = key.hashCode()
    val newSortedList = (sortedMapInt - kCode).toList.map {
      case (x, y) => (keyMap(x), y)
    }
    SortedParMap2(newSortedList)
  }

  def --(keys: GenTraversableOnce[Par]): SortedParMap2 = {
    val removeCodes = keys.toIterator.map(sort).map(_.hashCode()).toSet
    val newSortedList = sortedMapInt.filterNot(x => removeCodes(x._1)).toList.map {
      case (x, y) => (keyMap(x), y)
    }
    SortedParMap2(newSortedList)
  }

  def apply(par: Par): Par = {
    val kCode = sort(par).hashCode()
    sortedMapInt(kCode)
  }

  def contains(par: Par): Boolean = {
    val kCode = sort(par).hashCode()
    sortedMapInt.contains(kCode)
  }

  def empty: SortedParMap2 = SortedParMap2(List.empty[(Par, Par)])

  def get(key: Par): Option[Par] = {
    val kCode = sort(key).hashCode()
    sortedMapInt.get(kCode)
  }

  def getOrElse(key: Par, default: Par): Par = {
    val kCode = sort(key).hashCode()
    sortedMapInt.getOrElse(kCode, default)
  }

  def iterator: Iterator[(Par, Par)] = sortedList.toIterator

  def keys: Iterable[Par] = sortedList.map(_._1)

  def values: Iterable[Par] = sortedList.map(_._2)

  override def equals(that: Any): Boolean = that match {
    case spm: SortedParMap2 => spm.sortedMapInt == this.sortedMapInt
    case _                  => false
  }

  override def hashCode(): Int = sortedMapInt.hashCode()

  private def sort(par: Par): Par = Sortable[Par].sortMatch[Coeval](par).map(_.term).value()
}

object SortedParMap2 {
  //  def apply(map: Map[Par, Par]): SortedParMap = new SortedParMap(map)
  def apply(map: List[(Par, Par)]): SortedParMap2 = new SortedParMap2(map)

  def apply(seq: Seq[(Par, Par)]): SortedParMap2 =
    SortedParMap2(seq.toList)

  def empty: SortedParMap = SortedParMap(List.empty[(Par, Par)])
}
