package coop.rchain.models.rholang.sorter

import cats.effect.Sync
import coop.rchain.models.{AList, Par}
import coop.rchain.models.rholang.sorter.ScoredTerm._
import monix.eval.Coeval
import cats.implicits._

//FIXME the `.sort` methods in this file should return via F[_] : Sync, and the corresponding ParSet and ParMap should
//be constructed via factory methods also returning via F. Otherwise we risk StackOverflowErrors.
object ordering {

  implicit class ListSortOps(ps: List[Par]) {
    implicit val sync = implicitly[Sync[Coeval]]

    def sort: List[Par] = {
      val psSorted: List[Coeval[ScoredTerm[Par]]] =
        ps.map(par => Sortable[Par].sortMatch[Coeval](par))
      val coeval: Coeval[List[Par]] = for {
        parsSorted <- psSorted.sequence
      } yield parsSorted.sorted.map(_.term)

      coeval.value
    }
  }

  implicit class MapSortOps(ps: Map[Par, Par]) {
    implicit val sync = implicitly[Sync[Coeval]]

    def sortKeyValuePair(key: Par, value: Par): Coeval[ScoredTerm[(Par, Par)]] =
      for {
        sortedKey   <- Sortable.sortMatch(key)
        sortedValue <- Sortable.sortMatch(value)
      } yield ScoredTerm((sortedKey.term, sortedValue.term), sortedKey.score)

    def sort: List[(Par, Par)] = {
      val pairsSorted = ps.toList.map(kv => sortKeyValuePair(kv._1, kv._2))
      val coeval: Coeval[List[(Par, Par)]] = for {
        sequenced <- pairsSorted.sequence
      } yield sequenced.sorted.map(_.term)
      coeval.value
    }
  }

  implicit class AlistSortOps(ps: AList[String, Par]) {
    implicit val sync: Sync[Coeval] = implicitly[Sync[Coeval]]

    def sortPar(par: Par): Par = Sortable[Par].sortMatch[Coeval](par).map(_.term).value()

    def sort: AList[String, Par] = {
      def loop(subList: AList[String, Par]): AList[String, Par] = {
        val newList = subList.tree.sortBy(_._1).map {
          case (k, Left(list)) => (k, Left(loop(list)))
          case (k, Right(v))   => (k, Right(sortPar(v)))
        }
        AList(newList)
      }
      loop(ps)
    }
  }

}
