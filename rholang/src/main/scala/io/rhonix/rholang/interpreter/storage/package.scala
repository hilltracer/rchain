package io.rhonix.rholang.interpreter

import cats.effect.Async.catsStateTAsync
import cats.effect.Sync
import cats.mtl.implicits._
import cats.syntax.all._
import io.rhonix.metrics.Span
import io.rhonix.models.ProtoBindings.fromProto
import io.rhonix.models.Var.VarInstance.FreeVar
import io.rhonix.models._
import io.rhonix.models.protobuf.{BindPatternProto, ListParWithRandomProto, ParProto, TaggedContinuationProto}
import io.rhonix.models.rholang.implicits._
import io.rhonix.models.serialization.implicits.{mkProtobufInstance, mkRhoTypeInstance}
import io.rhonix.rholang.interpreter.matcher._
import io.rhonix.rspace.{Match => StorageMatch}
import io.rhonix.shared.Serialize

//noinspection ConvertExpressionToSAM
package object storage {

  /* Match instance */

  private def toSeq(fm: FreeMap, max: Int): Seq[Par] =
    (0 until max).map { (i: Int) =>
      fm.get(i) match {
        case Some(par) => par
        case None      => Par()
      }
    }

  def matchListPar[F[_]: Sync: Span]: StorageMatch[F, BindPattern, ListParWithRandom] =
    new StorageMatch[F, BindPattern, ListParWithRandom] {
      def get(
          pattern: BindPattern,
          data: ListParWithRandom
      ): F[Option[ListParWithRandom]] = {
        type R[A] = MatcherMonadT[F, A]
        implicit val matcherMonadError = implicitly[Sync[R]]
        for {
          matchResult <- runFirst[F, Seq[Par]](
                          SpatialMatcher
                            .foldMatch[R, Par, Par](
                              data.pars,
                              pattern.patterns,
                              pattern.remainder
                            )
                        )
        } yield {
          matchResult.map {
            case (freeMap, caughtRem) =>
              val remainderMap = pattern.remainder match {
                case Some(Var(FreeVar(level))) =>
                  val l: Expr = EList(caughtRem.toVector)
                  val p       = VectorPar()
                  freeMap + (level -> p.copy(exprs = p.exprs :+ l))
                case _ => freeMap
              }
              ListParWithRandom(
                toSeq(remainderMap, pattern.freeCount),
                data.randomState
              )
          }
        }
      }
    }

  /* Serialize instances */

  implicit val serializeBindPattern: Serialize[BindPattern] =
    mkRhoTypeInstance[BindPattern, BindPatternProto](fromProto)

  implicit val serializePar: Serialize[Par] =
    mkRhoTypeInstance[Par, ParProto](fromProto)

  implicit val serializePars: Serialize[ListParWithRandom] =
    mkRhoTypeInstance[ListParWithRandom, ListParWithRandomProto](fromProto)

  implicit val serializeTaggedContinuation: Serialize[TaggedContinuation] =
    mkRhoTypeInstance[TaggedContinuation, TaggedContinuationProto](fromProto)

  implicit val serializeBindPatternProto: Serialize[BindPatternProto] =
    mkProtobufInstance(BindPatternProto)

  implicit val serializeParProto: Serialize[ParProto] =
    mkProtobufInstance(ParProto)

  implicit val serializeParsProto: Serialize[ListParWithRandomProto] =
    mkProtobufInstance(ListParWithRandomProto)

  implicit val serializeTaggedContinuationProto: Serialize[TaggedContinuationProto] =
    mkProtobufInstance(TaggedContinuationProto)

}
