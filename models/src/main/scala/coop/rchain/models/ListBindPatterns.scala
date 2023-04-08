package coop.rchain.models

final case class ListBindPatterns(
    patterns: Seq[BindPattern] = Seq.empty
) extends RhoType
