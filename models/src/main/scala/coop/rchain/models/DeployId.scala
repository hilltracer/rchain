package coop.rchain.models

import com.google.protobuf.ByteString

final case class DeployId(
    sig: ByteString = ByteString.EMPTY
) extends ProtoConvertible
