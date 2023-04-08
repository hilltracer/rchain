package coop.rchain.models.protobuf

import scalapb.TypeMapper
import monix.eval.Coeval

object ParSetTypeMapper {
  implicit val parSetESetTypeMapper: TypeMapper[ESetProto, ParSetProto] =
    TypeMapper(esetToParSet)(parSetToESet)

  private[models] def esetToParSet(eset: ESetProto): ParSetProto =
    ParSetProto(
      ps = eset.ps,
      locallyFree = Coeval.delay(eset.locallyFree.get),
      connectiveUsed = eset.connectiveUsed,
      remainder = eset.remainder
    )

  private[models] def parSetToESet(parSet: ParSetProto): ESetProto =
    ESetProto(
      parSet.ps.sortedPars,
      parSet.locallyFree.value,
      parSet.connectiveUsed,
      parSet.remainder
    )
}
