package coop.rchain.models

import com.google.protobuf.ByteString

final case class GDeployerId(
    publicKey: ByteString = ByteString.EMPTY
) extends RhoType
