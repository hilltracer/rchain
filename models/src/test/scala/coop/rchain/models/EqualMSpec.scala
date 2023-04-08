package coop.rchain.models

import com.google.protobuf.ByteString
import coop.rchain.models.protobuf.ExprProto.ExprInstance.GInt
import coop.rchain.models.protobuf._
import coop.rchain.models.protobuf.testImplicitsProto._
import coop.rchain.models.testUtils.TestUtils.forAllSimilarA
import monix.eval.Coeval
import org.scalacheck.ScalacheckShapeless.arbitrarySingletonType
import org.scalacheck.{Arbitrary, Shrink}
import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.Function.tupled
import scala.collection.immutable.BitSet
import scala.reflect.ClassTag

class EqualMSpec extends AnyFlatSpec with ScalaCheckPropertyChecks with Matchers {

  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(sizeRange = 25, minSuccessful = 100)

  behavior of "EqualM"

  sameResultAsReference[ParProto]
  sameResultAsReference[ExprProto]
  sameResultAsReference[BindPatternProto]
  sameResultAsReference[BundleProto]
  sameResultAsReference[ConnectiveProto]
  sameResultAsReference[ConnectiveBodyProto]
  sameResultAsReference[EListProto]
  sameResultAsReference[EMapProto]
  sameResultAsReference[EMatchesProto]
  sameResultAsReference[EMethodProto]
  sameResultAsReference[ENeqProto]
  sameResultAsReference[ENotProto]
  sameResultAsReference[EOrProto]
  sameResultAsReference[ESetProto]
  sameResultAsReference[ETupleProto]
  sameResultAsReference[EVarProto]
  sameResultAsReference[GPrivateProto]
  sameResultAsReference[KeyValuePairProto]
  sameResultAsReference[ListBindPatternsProto]
  sameResultAsReference[MatchProto]
  sameResultAsReference[MatchCaseProto]
  sameResultAsReference[NewProto]
  sameResultAsReference[ParWithRandomProto]
  sameResultAsReference[PCostProto]
  sameResultAsReference[ReceiveProto]
  sameResultAsReference[ReceiveBindProto]
  sameResultAsReference[SendProto]
  sameResultAsReference[TaggedContinuationProto]
  sameResultAsReference[VarProto]
  sameResultAsReference[VarRefProto]
  sameResultAsReference[ParSetProto]
  sameResultAsReference[ParMapProto]

  sameResultAsReference[Int]
  sameResultAsReference[BigInt]
  sameResultAsReference[Long]
  sameResultAsReference[String]
  sameResultAsReference[ByteString]
  sameResultAsReference[BitSet]
  sameResultAsReference[AlwaysEqual[BitSet]]

  sameResultAsReference[SortedParHashSetProto]
  sameResultAsReference[SortedParMapProto]

  //fixed regressions / corner cases:
  sameResultAsReference(GInt(-1), GInt(-1))
  sameResultAsReference(ExprProto(GInt(-1)), ExprProto(GInt(-1)))

  def sameResultAsReference[A <: Any: EqualM: Arbitrary: Shrink: Pretty](
      implicit tag: ClassTag[A]
  ): Unit =
    it must s"provide same results as equals for ${tag.runtimeClass.getSimpleName}" in {
      forAllSimilarA[A]((x, y) => sameResultAsReference(x, y))
    }

  private def sameResultAsReference[A <: Any: EqualM: Pretty](x: A, y: A): Assertion = {
    // We are going to override the generated hashCode for our generated AST classes,
    // so in this test we rely on the underlying implementation from ScalaRuntime,
    // and hard-code the current definition for the handmade AST classes.

    def reference(self: Any, other: Any): Boolean = (self, other) match {
      case (left: ParSetProto, right: ParSetProto) =>
        Equiv.by((x: ParSetProto) => (x.ps, x.remainder, x.connectiveUsed)).equiv(left, right)
      case (left: ParMapProto, right: ParMapProto) =>
        Equiv.by((x: ParMapProto) => (x.ps, x.remainder, x.connectiveUsed)).equiv(left, right)
      case (left: Product, right: Product) =>
        left.getClass.isInstance(other) &&
          left.productIterator
            .zip(right.productIterator)
            .forall(tupled(reference))
      case _ => self.equals(other)
    }

    val referenceResult = reference(x, y)
    val equalsResult    = x == y
    val equalMResult    = EqualM[A].equal[Coeval](x, y).value

    withClue(
      s"""
         |
         |Inconsistent results:
         |
         |     reference(x, y): $referenceResult
         |              x == y: $equalsResult
         |  EqualM.equal(x, y): $equalMResult
         |
         |Test data used:
         |
         |${Pretty.pretty(x)}
         |
         |and
         |
         |${Pretty.pretty(y)}
         |
         |""".stripMargin
    ) {
      // With this check we know that:
      //   EqualM.equal[Id] == _.equals == reference
      // This makes this test valid both before and after we override the generated hashCode
      // (which is long done when you're reading this).
      referenceResult should be(equalsResult)
      equalMResult should be(referenceResult)
    }
  }

}
