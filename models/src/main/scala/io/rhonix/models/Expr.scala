package io.rhonix.models

import com.google.protobuf.ByteString

/** Any process may be an operand to an expression.
  * Only processes equivalent to a ground process of compatible type will reduce.
  */
final case class Expr(
    exprInstance: Expr.ExprInstance = Expr.ExprInstance.Empty
) extends RhoType

object Expr {
  sealed trait ExprInstance {
    def isEmpty: Boolean                             = false
    def isDefined: Boolean                           = true
    def isGBool: Boolean                             = false
    def isGInt: Boolean                              = false
    def isGBigInt: Boolean                           = false
    def isGString: Boolean                           = false
    def isGUri: Boolean                              = false
    def isGByteArray: Boolean                        = false
    def isENotBody: Boolean                          = false
    def isENegBody: Boolean                          = false
    def isEMultBody: Boolean                         = false
    def isEDivBody: Boolean                          = false
    def isEPlusBody: Boolean                         = false
    def isEMinusBody: Boolean                        = false
    def isELtBody: Boolean                           = false
    def isELteBody: Boolean                          = false
    def isEGtBody: Boolean                           = false
    def isEGteBody: Boolean                          = false
    def isEEqBody: Boolean                           = false
    def isENeqBody: Boolean                          = false
    def isEAndBody: Boolean                          = false
    def isEOrBody: Boolean                           = false
    def isEVarBody: Boolean                          = false
    def isEListBody: Boolean                         = false
    def isETupleBody: Boolean                        = false
    def isESetBody: Boolean                          = false
    def isEMapBody: Boolean                          = false
    def isEMethodBody: Boolean                       = false
    def isEMatchesBody: Boolean                      = false
    def isEPercentPercentBody: Boolean               = false
    def isEPlusPlusBody: Boolean                     = false
    def isEMinusMinusBody: Boolean                   = false
    def isEModBody: Boolean                          = false
    def isEShortAndBody: Boolean                     = false
    def isEShortOrBody: Boolean                      = false
    def gBool: Option[Boolean]                       = None
    def gInt: Option[Long]                           = None
    def gBigInt: Option[BigInt]                      = None
    def gString: Option[String]                      = None
    def gUri: Option[String]                         = None
    def gByteArray: Option[ByteString]               = None
    def eNotBody: Option[ENot]                       = None
    def eNegBody: Option[ENeg]                       = None
    def eMultBody: Option[EMult]                     = None
    def eDivBody: Option[EDiv]                       = None
    def ePlusBody: Option[EPlus]                     = None
    def eMinusBody: Option[EMinus]                   = None
    def eLtBody: Option[ELt]                         = None
    def eLteBody: Option[ELte]                       = None
    def eGtBody: Option[EGt]                         = None
    def eGteBody: Option[EGte]                       = None
    def eEqBody: Option[EEq]                         = None
    def eNeqBody: Option[ENeq]                       = None
    def eAndBody: Option[EAnd]                       = None
    def eOrBody: Option[EOr]                         = None
    def eVarBody: Option[EVar]                       = None
    def eListBody: Option[EList]                     = None
    def eTupleBody: Option[ETuple]                   = None
    def eSetBody: Option[ParSet]                     = None
    def eMapBody: Option[ParMap]                     = None
    def eMethodBody: Option[EMethod]                 = None
    def eMatchesBody: Option[EMatches]               = None
    def ePercentPercentBody: Option[EPercentPercent] = None
    def ePlusPlusBody: Option[EPlusPlus]             = None
    def eMinusMinusBody: Option[EMinusMinus]         = None
    def eModBody: Option[EMod]                       = None
    def eShortAndBody: Option[EShortAnd]             = None
    def eShortOrBody: Option[EShortOr]               = None
  }
  object ExprInstance {
    case object Empty extends Expr.ExprInstance {
      type ValueType = Nothing
      override def isEmpty: Boolean   = true
      override def isDefined: Boolean = false
    }
    final case class GBool(value: Boolean) extends Expr.ExprInstance {
      type ValueType = Boolean
      override def isGBool: Boolean       = true
      override def gBool: Option[Boolean] = Some(value)
    }
    final case class GInt(value: Long) extends Expr.ExprInstance {
      type ValueType = Long
      override def isGInt: Boolean    = true
      override def gInt: Option[Long] = Some(value)
    }
    final case class GBigInt(value: BigInt) extends Expr.ExprInstance {
      type ValueType = BigInt
      override def isGBigInt: Boolean      = true
      override def gBigInt: Option[BigInt] = Some(value)
    }
    final case class GString(value: String) extends Expr.ExprInstance {
      type ValueType = String
      override def isGString: Boolean      = true
      override def gString: Option[String] = Some(value)
    }
    final case class GUri(value: String) extends Expr.ExprInstance {
      type ValueType = String
      override def isGUri: Boolean      = true
      override def gUri: Option[String] = Some(value)
    }
    final case class GByteArray(value: ByteString) extends Expr.ExprInstance {
      type ValueType = ByteString
      override def isGByteArray: Boolean = true
      override def gByteArray: Option[ByteString] =
        Some(value)
    }
    final case class ENotBody(value: ENot) extends Expr.ExprInstance {
      type ValueType = ENot
      override def isENotBody: Boolean    = true
      override def eNotBody: Option[ENot] = Some(value)
    }
    final case class ENegBody(value: ENeg) extends Expr.ExprInstance {
      type ValueType = ENeg
      override def isENegBody: Boolean    = true
      override def eNegBody: Option[ENeg] = Some(value)
    }
    final case class EMultBody(value: EMult) extends Expr.ExprInstance {
      type ValueType = EMult
      override def isEMultBody: Boolean     = true
      override def eMultBody: Option[EMult] = Some(value)
    }
    final case class EDivBody(value: EDiv) extends Expr.ExprInstance {
      type ValueType = EDiv
      override def isEDivBody: Boolean    = true
      override def eDivBody: Option[EDiv] = Some(value)
    }
    final case class EPlusBody(value: EPlus) extends Expr.ExprInstance {
      type ValueType = EPlus
      override def isEPlusBody: Boolean     = true
      override def ePlusBody: Option[EPlus] = Some(value)
    }
    final case class EMinusBody(value: EMinus) extends Expr.ExprInstance {
      type ValueType = EMinus
      override def isEMinusBody: Boolean      = true
      override def eMinusBody: Option[EMinus] = Some(value)
    }
    final case class ELtBody(value: ELt) extends Expr.ExprInstance {
      type ValueType = ELt
      override def isELtBody: Boolean   = true
      override def eLtBody: Option[ELt] = Some(value)
    }
    final case class ELteBody(value: ELte) extends Expr.ExprInstance {
      type ValueType = ELte
      override def isELteBody: Boolean    = true
      override def eLteBody: Option[ELte] = Some(value)
    }
    final case class EGtBody(value: EGt) extends Expr.ExprInstance {
      type ValueType = EGt
      override def isEGtBody: Boolean   = true
      override def eGtBody: Option[EGt] = Some(value)
    }
    final case class EGteBody(value: EGte) extends Expr.ExprInstance {
      type ValueType = EGte
      override def isEGteBody: Boolean    = true
      override def eGteBody: Option[EGte] = Some(value)
    }
    final case class EEqBody(value: EEq) extends Expr.ExprInstance {
      type ValueType = EEq
      override def isEEqBody: Boolean   = true
      override def eEqBody: Option[EEq] = Some(value)
    }
    final case class ENeqBody(value: ENeq) extends Expr.ExprInstance {
      type ValueType = ENeq
      override def isENeqBody: Boolean    = true
      override def eNeqBody: Option[ENeq] = Some(value)
    }
    final case class EAndBody(value: EAnd) extends Expr.ExprInstance {
      type ValueType = EAnd
      override def isEAndBody: Boolean    = true
      override def eAndBody: Option[EAnd] = Some(value)
    }
    final case class EOrBody(value: EOr) extends Expr.ExprInstance {
      type ValueType = EOr
      override def isEOrBody: Boolean   = true
      override def eOrBody: Option[EOr] = Some(value)
    }
    final case class EVarBody(value: EVar) extends Expr.ExprInstance {
      type ValueType = EVar
      override def isEVarBody: Boolean    = true
      override def eVarBody: Option[EVar] = Some(value)
    }
    final case class EListBody(value: EList) extends Expr.ExprInstance {
      type ValueType = EList
      override def isEListBody: Boolean     = true
      override def eListBody: Option[EList] = Some(value)
    }
    final case class ETupleBody(value: ETuple) extends Expr.ExprInstance {
      type ValueType = ETuple
      override def isETupleBody: Boolean      = true
      override def eTupleBody: Option[ETuple] = Some(value)
    }
    final case class ESetBody(value: ParSet) extends Expr.ExprInstance {
      type ValueType = ParSet
      override def isESetBody: Boolean      = true
      override def eSetBody: Option[ParSet] = Some(value)
    }
    final case class EMapBody(value: ParMap) extends Expr.ExprInstance {
      type ValueType = ParMap
      override def isEMapBody: Boolean      = true
      override def eMapBody: Option[ParMap] = Some(value)
    }
    final case class EMethodBody(value: EMethod) extends Expr.ExprInstance {
      type ValueType = EMethod
      override def isEMethodBody: Boolean       = true
      override def eMethodBody: Option[EMethod] = Some(value)
    }
    final case class EMatchesBody(value: EMatches) extends Expr.ExprInstance {
      type ValueType = EMatches
      override def isEMatchesBody: Boolean        = true
      override def eMatchesBody: Option[EMatches] = Some(value)
    }
    final case class EPercentPercentBody(value: EPercentPercent) extends Expr.ExprInstance {
      type ValueType = EPercentPercent
      override def isEPercentPercentBody: Boolean               = true
      override def ePercentPercentBody: Option[EPercentPercent] = Some(value)
    }
    final case class EPlusPlusBody(value: EPlusPlus) extends Expr.ExprInstance {
      type ValueType = EPlusPlus
      override def isEPlusPlusBody: Boolean         = true
      override def ePlusPlusBody: Option[EPlusPlus] = Some(value)
    }
    final case class EMinusMinusBody(value: EMinusMinus) extends Expr.ExprInstance {
      type ValueType = EMinusMinus
      override def isEMinusMinusBody: Boolean           = true
      override def eMinusMinusBody: Option[EMinusMinus] = Some(value)
    }
    final case class EModBody(value: EMod) extends Expr.ExprInstance {
      type ValueType = EMod
      override def isEModBody: Boolean    = true
      override def eModBody: Option[EMod] = Some(value)
    }
    final case class EShortAndBody(value: EShortAnd) extends Expr.ExprInstance {
      type ValueType = EShortAnd
      override def isEShortAndBody: Boolean         = true
      override def eShortAndBody: Option[EShortAnd] = Some(value)
    }
    final case class EShortOrBody(value: EShortOr) extends Expr.ExprInstance {
      type ValueType = EShortOr
      override def isEShortOrBody: Boolean        = true
      override def eShortOrBody: Option[EShortOr] = Some(value)
    }
  }
}
