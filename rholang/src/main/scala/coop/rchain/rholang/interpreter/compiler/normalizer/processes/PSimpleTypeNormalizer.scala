package coop.rchain.rholang.interpreter.compiler.normalizer.processes

import cats.effect.Sync
import cats.syntax.all._
import coop.rchain.models.rholangn._
import coop.rchain.rholang.ast.rholang_mercury.Absyn._
import coop.rchain.rholang.interpreter.compiler.{ProcVisitInputs, ProcVisitOutputs}

object PSimpleTypeNormalizer {
  def normalize[F[_]: Sync](p: PSimpleType, input: ProcVisitInputs): F[ProcVisitOutputs] =
    p.simpletype_ match {
      case _: SimpleTypeBool =>
        ProcVisitOutputs(input.par |+| ConnBoolN, input.freeMap).pure[F]
      case _: SimpleTypeInt =>
        ProcVisitOutputs(input.par |+| ConnIntN, input.freeMap).pure[F]
      case _: SimpleTypeBigInt =>
        ProcVisitOutputs(input.par |+| ConnBigIntN, input.freeMap).pure[F]
      case _: SimpleTypeString =>
        ProcVisitOutputs(input.par |+| ConnStringN, input.freeMap).pure[F]
      case _: SimpleTypeUri =>
        ProcVisitOutputs(input.par |+| ConnUriN, input.freeMap).pure[F]
      case _: SimpleTypeByteArray =>
        ProcVisitOutputs(input.par |+| ConnByteArrayN, input.freeMap).pure[F]
    }
}
