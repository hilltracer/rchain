package io.rhonix.models

final case class ConnectiveBody(
    ps: Seq[Par] = Seq.empty
) extends RhoType
