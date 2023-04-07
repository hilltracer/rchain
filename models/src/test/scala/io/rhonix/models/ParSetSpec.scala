package io.rhonix.models

import io.rhonix.models.Expr.ExprInstance.{ESetBody, GInt}
import io.rhonix.models.ProtoBindings.toProto
import io.rhonix.models.Var.VarInstance.BoundVar
import io.rhonix.models.protobuf.ExprProto
import io.rhonix.models.rholang.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.BitSet

class ParSetSpec extends AnyFlatSpec with Matchers {

  "ParSet" should "serialize like raw ESet" in {
    val parGround =
      ParSet(
        Seq[Par](
          GInt(2),
          GInt(1),
          EMethod("nth", EVar(BoundVar(2)), List(GInt(1)), locallyFree = BitSet(2)),
          ParSet(Seq[Par](GInt(1), GInt(2))),
          ParSet(Seq[Par](GInt(1), GInt(1)))
        )
      )

    val sortedParGround =
      ParSet(
        Seq[Par](
          GInt(1),
          GInt(2),
          ParSet(Seq[Par](GInt(1))),
          ParSet(Seq[Par](GInt(1), GInt(2))),
          EMethod("nth", EVar(BoundVar(2)), List(GInt(1)), locallyFree = BitSet(2))
        )
      )

    val expr1 = Expr(ESetBody(parGround))
    val expr2 = Expr(ESetBody(sortedParGround))

    // `ParSet` should be mapped to `ESet` using `ParSetTypeMapper`
    java.util.Arrays.equals(toProto(expr1).toByteArray, toProto(expr2).toByteArray) should be(true)

    // roundtrip serialization
    ExprProto.parseFrom(toProto(expr2).toByteArray) should be(toProto(expr2))
  }

  it should "properly calculate locallyFree from enclosed `Par`s" in {
    val parSet = ParSet(
      Seq[Par](
        GInt(2),
        GInt(1),
        EMethod("nth", EVar(BoundVar(2)), List(GInt(1)), locallyFree = BitSet(2)),
        ParSet(Seq[Par](GInt(1), GInt(2))),
        ParSet(Seq[Par](GInt(1), GInt(1)))
      )
    )

    parSet.locallyFree.value should ===(BitSet(2))
  }

}
