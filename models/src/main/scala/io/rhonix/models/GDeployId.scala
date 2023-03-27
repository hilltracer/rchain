package io.rhonix.models

import com.google.protobuf.ByteString

final case class GDeployId(
    sig: ByteString = ByteString.EMPTY
) extends RhoType
