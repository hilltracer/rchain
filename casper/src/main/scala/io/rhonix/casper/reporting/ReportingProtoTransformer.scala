package io.rhonix.casper.reporting

import io.rhonix.casper.protocol.{
  PeekProto,
  ReportCommProto,
  ReportConsumeProto,
  ReportEventProto,
  ReportProduceProto
}
import io.rhonix.models.ProtoBindings.toProto
import io.rhonix.models.{BindPattern, ListParWithRandom, Par, TaggedContinuation}
import io.rhonix.rspace.{ReportingRspace, ReportingTransformer}

class ReportingProtoTransformer
    extends ReportingTransformer[
      Par,
      BindPattern,
      ListParWithRandom,
      TaggedContinuation,
      ReportEventProto
    ] {
  override def serializeConsume(
      rc: RhoReportingConsume
  ): ReportConsumeProto =
    ReportConsumeProto(
      rc.channels.map(toProto),
      rc.patterns.map(toProto),
      rc.peeks.map(PeekProto(_))
    )

  override def serializeProduce(rp: RhoReportingProduce): ReportProduceProto =
    ReportProduceProto(channel = toProto(rp.channel), data = toProto(rp.data))

  override def serializeComm(rcm: RhoReportingComm): ReportCommProto =
    ReportCommProto(
      consume = serializeConsume(rcm.consume),
      produces = rcm.produces.map(serializeProduce).toList
    )

  override def transformEvent(re: ReportingRspace.ReportingEvent): ReportEventProto =
    super.transformEvent(re)
}
