package io.rhonix.models

import com.google.protobuf.ByteString

final case class DeployerId(
    publicKey: ByteString = ByteString.EMPTY
) extends ProtoConvertible
