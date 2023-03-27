package io.rhonix.models

final case class ListBindPatterns(
    patterns: Seq[BindPattern] = Seq.empty
) extends RhoType
