package coop.rchain.models

/** *
  * Either rholang code or code built in to the interpreter.
  */
final case class TaggedContinuation(
    taggedCont: TaggedContinuation.TaggedCont = TaggedContinuation.TaggedCont.Empty
) extends RhoType
object TaggedContinuation {
  sealed trait TaggedCont extends _root_.scalapb.GeneratedOneof {
    def isEmpty: Boolean               = false
    def isDefined: Boolean             = true
    def isParBody: Boolean             = false
    def isScalaBodyRef: Boolean        = false
    def parBody: Option[ParWithRandom] = None
    def scalaBodyRef: Option[Long]     = None
  }
  object TaggedCont {
    case object Empty extends TaggedContinuation.TaggedCont {
      type ValueType = Nothing
      override def isEmpty: Boolean   = true
      override def isDefined: Boolean = false
      override def number: Int        = 0
      override def value: Nothing =
        ???
    }
    final case class ParBody(value: ParWithRandom) extends TaggedContinuation.TaggedCont {
      type ValueType = ParWithRandom
      override def isParBody: Boolean             = true
      override def parBody: Option[ParWithRandom] = Some(value)
      override def number: Int                    = 1
    }
    final case class ScalaBodyRef(value: Long) extends TaggedContinuation.TaggedCont {
      type ValueType = Long
      override def isScalaBodyRef: Boolean    = true
      override def scalaBodyRef: Option[Long] = Some(value)
      override def number: Int                = 2
    }
  }
}
