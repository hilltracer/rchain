package coop.rchain.models

/** While we use vars in both positions, when producing the normalized
  * representation we need a discipline to track whether a var is a name or a
  * process.
  * These are DeBruijn levels
  */
final case class Var(
    varInstance: Var.VarInstance = Var.VarInstance.Empty
) extends RhoType

object Var {
  sealed trait VarInstance extends _root_.scalapb.GeneratedOneof {
    def isEmpty: _root_.scala.Boolean                   = false
    def isDefined: _root_.scala.Boolean                 = true
    def isBoundVar: _root_.scala.Boolean                = false
    def isFreeVar: _root_.scala.Boolean                 = false
    def isWildcard: _root_.scala.Boolean                = false
    def boundVar: _root_.scala.Option[_root_.scala.Int] = _root_.scala.None
    def freeVar: _root_.scala.Option[_root_.scala.Int]  = _root_.scala.None
    def wildcard: _root_.scala.Option[Var.WildcardMsg]  = _root_.scala.None
  }
  object VarInstance {
    case object Empty extends Var.VarInstance {
      type ValueType = _root_.scala.Nothing
      override def isEmpty: _root_.scala.Boolean   = true
      override def isDefined: _root_.scala.Boolean = false
      override def number: _root_.scala.Int        = 0
      override def value: _root_.scala.Nothing =
        ???
    }
    final case class BoundVar(value: _root_.scala.Int) extends Var.VarInstance {
      type ValueType = _root_.scala.Int
      override def isBoundVar: _root_.scala.Boolean                = true
      override def boundVar: _root_.scala.Option[_root_.scala.Int] = Some(value)
      override def number: _root_.scala.Int                        = 1
    }
    final case class FreeVar(value: _root_.scala.Int) extends Var.VarInstance {
      type ValueType = _root_.scala.Int
      override def isFreeVar: _root_.scala.Boolean                = true
      override def freeVar: _root_.scala.Option[_root_.scala.Int] = Some(value)
      override def number: _root_.scala.Int                       = 2
    }
    final case class Wildcard(value: Var.WildcardMsg) extends Var.VarInstance {
      type ValueType = Var.WildcardMsg
      override def isWildcard: _root_.scala.Boolean               = true
      override def wildcard: _root_.scala.Option[Var.WildcardMsg] = Some(value)
      override def number: _root_.scala.Int                       = 3
    }
  }
  final case class WildcardMsg()
}
