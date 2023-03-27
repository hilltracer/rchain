package io.rhonix.models.serialization

import com.google.protobuf.CodedInputStream
import io.rhonix.models.ProtoBindings._
import io.rhonix.models.{StacksafeMessage, _}
import io.rhonix.shared.Serialize
import monix.eval.Coeval
import scalapb.GeneratedMessageCompanion
import scodec.bits.ByteVector

import scala.language.implicitConversions

object implicits {

  implicit def mkProtobufInstance[T <: StacksafeMessage[T]: GeneratedMessageCompanion] =
    new Serialize[T] {

      override def encode(a: T): ByteVector =
        ByteVector.view(ProtoM.toByteArray(a).value())

      override def decode(bytes: ByteVector): Either[Throwable, T] = {
        val companion = implicitly[GeneratedMessageCompanion[T]]
        val buffer    = CodedInputStream.newInstance(bytes.toArray)
        companion.defaultInstance.mergeFromM[Coeval](buffer).runAttempt()
      }
    }

  implicit def mkRhoTypeInstance[TRho <: ProtoConvertible, TProto <: StacksafeMessage[TProto]: GeneratedMessageCompanion](
      convert: TProto => TRho
  ): Serialize[TRho] =
    new Serialize[TRho] {
      override def encode(a: TRho): ByteVector =
        ByteVector.view(ProtoM.toByteArray(toProto(a)).value())

      override def decode(bytes: ByteVector): Either[Throwable, TRho] = {
        val companion = implicitly[GeneratedMessageCompanion[TProto]]
        val buffer    = CodedInputStream.newInstance(bytes.toArray)
        val protoRes  = companion.defaultInstance.mergeFromM[Coeval](buffer).runAttempt()
        protoRes.map(convert)
      }
    }
}
