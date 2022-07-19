package coop.rchain.models

import coop.rchain.models.rholang.sorter.ordering._

final case class ParAMap(ps: AMap[String, Par]) {
  lazy val sortedAList: AList[String, Par] = ps.toList.sort

  override def equals(that: Any): Boolean = that match {
    case spl: ParAMap => spl.sortedAList == this.sortedAList
    case _            => false
  }

  override def hashCode(): Int = sortedAList.hashCode()
}

object ParAMap {
  def apply(branching: List[(Option[String], String)], records: Map[String, Map[String, Par]]) =
    new ParAMap(AMap[String, Par](branching, records))

  def apply(list: AList[String, Par]): ParAMap =
    ParAMap(AMap[String, Par](list))
}
