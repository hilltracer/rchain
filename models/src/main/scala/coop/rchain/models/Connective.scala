package coop.rchain.models

final case class Connective(
    connectiveInstance: Connective.ConnectiveInstance = Connective.ConnectiveInstance.Empty
) extends RhoType

object Connective {
  sealed trait ConnectiveInstance {
    def isEmpty: Boolean                    = false
    def isDefined: Boolean                  = true
    def isConnAndBody: Boolean              = false
    def isConnOrBody: Boolean               = false
    def isConnNotBody: Boolean              = false
    def isVarRefBody: Boolean               = false
    def isConnBool: Boolean                 = false
    def isConnInt: Boolean                  = false
    def isConnBigInt: Boolean               = false
    def isConnString: Boolean               = false
    def isConnUri: Boolean                  = false
    def isConnByteArray: Boolean            = false
    def connAndBody: Option[ConnectiveBody] = None
    def connOrBody: Option[ConnectiveBody]  = None
    def connNotBody: Option[Par]            = None
    def varRefBody: Option[VarRef]          = None
    def connBool: Option[Boolean]           = None
    def connInt: Option[Boolean]            = None
    def connBigInt: Option[Boolean]         = None
    def connString: Option[Boolean]         = None
    def connUri: Option[Boolean]            = None
    def connByteArray: Option[Boolean]      = None
  }
  object ConnectiveInstance {
    case object Empty extends Connective.ConnectiveInstance {
      override def isEmpty: Boolean   = true
      override def isDefined: Boolean = false
    }
    final case class ConnAndBody(value: ConnectiveBody) extends Connective.ConnectiveInstance {
      override def isConnAndBody: Boolean              = true
      override def connAndBody: Option[ConnectiveBody] = Some(value)
    }
    final case class ConnOrBody(value: ConnectiveBody) extends Connective.ConnectiveInstance {
      override def isConnOrBody: Boolean              = true
      override def connOrBody: Option[ConnectiveBody] = Some(value)
    }
    final case class ConnNotBody(value: Par) extends Connective.ConnectiveInstance {
      override def isConnNotBody: Boolean   = true
      override def connNotBody: Option[Par] = Some(value)
    }
    final case class VarRefBody(value: VarRef) extends Connective.ConnectiveInstance {
      override def isVarRefBody: Boolean      = true
      override def varRefBody: Option[VarRef] = Some(value)
    }
    final case class ConnBool(value: Boolean) extends Connective.ConnectiveInstance {
      override def isConnBool: Boolean       = true
      override def connBool: Option[Boolean] = Some(value)
    }
    final case class ConnInt(value: Boolean) extends Connective.ConnectiveInstance {
      override def isConnInt: Boolean       = true
      override def connInt: Option[Boolean] = Some(value)
    }
    final case class ConnBigInt(value: Boolean) extends Connective.ConnectiveInstance {
      override def isConnBigInt: Boolean       = true
      override def connBigInt: Option[Boolean] = Some(value)
    }
    final case class ConnString(value: Boolean) extends Connective.ConnectiveInstance {
      override def isConnString: Boolean       = true
      override def connString: Option[Boolean] = Some(value)
    }
    final case class ConnUri(value: Boolean) extends Connective.ConnectiveInstance {
      override def isConnUri: Boolean       = true
      override def connUri: Option[Boolean] = Some(value)
    }
    final case class ConnByteArray(value: Boolean) extends Connective.ConnectiveInstance {
      override def isConnByteArray: Boolean       = true
      override def connByteArray: Option[Boolean] = Some(value)
    }
  }
}
