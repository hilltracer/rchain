package coop.rchain.casper.util

object Sorting {

  implicit val byteArrayOrdering =
    Ordering.by((_: Array[Byte]).toIndexedSeq)(Ordering.Implicits.seqOrdering)
}
