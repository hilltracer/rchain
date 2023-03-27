package io.rhonix.rspace

import io.rhonix.models._
import io.rhonix.models.protobuf._
import org.scalacheck._
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck._

import scala.reflect.ClassTag

class KryoRoundTripTest extends AnyFlatSpec with ScalaCheckPropertyChecks with Matchers {
  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(minSuccessful = 50, sizeRange = 250)

  implicit val exprSerialize        = KryoSerializers.serializer(classOf[ExprProto])
  implicit val unfSerialize         = KryoSerializers.serializer(classOf[GUnforgeableProto])
  implicit val parSerialize         = KryoSerializers.serializer(classOf[ParProto])
  implicit val bindPatternSerialize = KryoSerializers.serializer(classOf[BindPatternProto])
  implicit val listParWithRandomSerialize =
    KryoSerializers.serializer(classOf[ListParWithRandomProto])
  implicit val taggedContinuationSerialize =
    KryoSerializers.serializer(classOf[TaggedContinuationProto])

  def roundTrip[A](in: A)(implicit s: Serialize2ByteBuffer[A]): Assertion = {
    val meta = s.encode(in)
    val out  = s.decode(meta)
    assert(out == in)
  }

  def roundTripSerialization[A: Arbitrary: Shrink](
      implicit s: Serialize2ByteBuffer[A],
      tag: ClassTag[A]
  ): Unit =
    it must s"work for ${tag.runtimeClass.getSimpleName}" in {
      forAll { a: A =>
        roundTrip(a)
      }
    }

  import io.rhonix.models.protobuf.testImplicitsProto._
  roundTripSerialization[ExprProto]
  roundTripSerialization[ParProto]
  roundTripSerialization[BindPatternProto]
  roundTripSerialization[ListParWithRandomProto]
  roundTripSerialization[TaggedContinuationProto]
}
