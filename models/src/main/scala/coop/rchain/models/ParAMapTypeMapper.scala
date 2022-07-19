package coop.rchain.models

import scalapb.TypeMapper

object ParAMapTypeMapper {
  implicit val ParAMapEAMapTypeMapper: TypeMapper[EAMap, ParAMap] =
    TypeMapper(eAMapToParAMap)(parAMapToEAMap)

  private[models] def eAMapToParAMap(eAMap: EAMap): ParAMap =
    ???

  private[models] def parAMapToEAMap(parAMap: ParAMap): EAMap =
    ???

  private[models] def unzip(kvp: KeyValuePair): (Par, Par) = (kvp.key, kvp.value)

  private[models] def zip(k: Par, v: Par): KeyValuePair = KeyValuePair(k, v)
}
