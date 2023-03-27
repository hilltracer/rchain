package io.rhonix.models

/** Unforgeable names resulting from `new x { ... }`
  * These should only occur as the program is being evaluated. There is no way in
  * the grammar to construct them.
  */
final case class GUnforgeable(
    unfInstance: GUnforgeable.UnfInstance = GUnforgeable.UnfInstance.Empty
) extends RhoType

object GUnforgeable {
  sealed trait UnfInstance extends _root_.scalapb.GeneratedOneof {
    def isEmpty: Boolean                         = false
    def isDefined: Boolean                       = true
    def isGPrivateBody: Boolean                  = false
    def isGDeployIdBody: Boolean                 = false
    def isGDeployerIdBody: Boolean               = false
    def isGSysAuthTokenBody: Boolean             = false
    def gPrivateBody: Option[GPrivate]           = None
    def gDeployIdBody: Option[GDeployId]         = None
    def gDeployerIdBody: Option[GDeployerId]     = None
    def gSysAuthTokenBody: Option[GSysAuthToken] = None
  }
  object UnfInstance {
    case object Empty extends GUnforgeable.UnfInstance {
      type ValueType = Nothing
      override def isEmpty: Boolean   = true
      override def isDefined: Boolean = false
      override def number: Int        = 0
      override def value: Nothing =
        ???
    }
    final case class GPrivateBody(value: GPrivate) extends GUnforgeable.UnfInstance {
      type ValueType = GPrivate
      override def isGPrivateBody: Boolean        = true
      override def gPrivateBody: Option[GPrivate] = Some(value)
      override def number: Int                    = 1
    }
    final case class GDeployIdBody(value: GDeployId) extends GUnforgeable.UnfInstance {
      type ValueType = GDeployId
      override def isGDeployIdBody: Boolean         = true
      override def gDeployIdBody: Option[GDeployId] = Some(value)
      override def number: Int                      = 2
    }
    final case class GDeployerIdBody(value: GDeployerId) extends GUnforgeable.UnfInstance {
      type ValueType = GDeployerId
      override def isGDeployerIdBody: Boolean           = true
      override def gDeployerIdBody: Option[GDeployerId] = Some(value)
      override def number: Int                          = 3
    }
    final case class GSysAuthTokenBody(value: GSysAuthToken) extends GUnforgeable.UnfInstance {
      type ValueType = GSysAuthToken
      override def isGSysAuthTokenBody: Boolean             = true
      override def gSysAuthTokenBody: Option[GSysAuthToken] = Some(value)
      override def number: Int                              = 4
    }
  }
}
