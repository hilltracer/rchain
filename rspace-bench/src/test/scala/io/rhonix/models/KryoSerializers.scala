package io.rhonix.models

import java.nio.ByteBuffer
import scala.reflect.ClassTag
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.esotericsoftware.kryo.io._
import io.rhonix.models.protobuf._
import org.objenesis.strategy.StdInstantiatorStrategy

trait Serialize2ByteBuffer[A] {
  def encode(a: A): ByteBuffer
  def decode(bytes: ByteBuffer): A

}

class DefaultSerializer[T](implicit tag: ClassTag[T]) extends Serializer[T] {

  def defaultSerializer(kryo: Kryo): Serializer[T] =
    kryo
      .getDefaultSerializer(tag.runtimeClass)
      .asInstanceOf[Serializer[T]]

  override def write(kryo: Kryo, output: Output, e: T): Unit =
    defaultSerializer(kryo).write(kryo, output, e)

  override def read(
      kryo: Kryo,
      input: Input,
      `type`: Class[_ <: T]
  ): T = defaultSerializer(kryo).read(kryo, input, `type`)

}

object KryoSerializers {

  object ParMapSerializer extends Serializer[ParMapProto] {
    import io.rhonix.models.protobuf.ParMapTypeMapper._

    override def write(kryo: Kryo, output: Output, parMap: ParMapProto): Unit =
      kryo.writeObject(output, parMapToEMap(parMap))

    override def read(kryo: Kryo, input: Input, `type`: Class[_ <: ParMapProto]): ParMapProto =
      emapToParMap(kryo.readObject(input, classOf[EMapProto]))
  }

  object ParSetSerializer extends Serializer[ParSetProto] {
    import io.rhonix.models.protobuf.ParSetTypeMapper._

    override def write(kryo: Kryo, output: Output, parSet: ParSetProto): Unit =
      kryo.writeObject(output, parSetToESet(parSet))

    override def read(kryo: Kryo, input: Input, `type`: Class[_ <: ParSetProto]): ParSetProto =
      esetToParSet(kryo.readObject(input, classOf[ESetProto]))
  }

  def emptyReplacingSerializer[T](thunk: T => Boolean, replaceWith: T)(implicit tag: ClassTag[T]) =
    new DefaultSerializer[T] {
      override def read(
          kryo: Kryo,
          input: Input,
          `type`: Class[_ <: T]
      ): T = {
        val read = super.read(kryo, input, `type`)
        if (thunk(read))
          replaceWith
        else read
      }
    }

  val TaggedContinuationSerializer =
    emptyReplacingSerializer[TaggedContinuationProto](
      _.taggedCont.isEmpty,
      TaggedContinuationProto()
    )

  val VarSerializer =
    emptyReplacingSerializer[VarProto](_.varInstance.isEmpty, VarProto())

  val ExprSerializer =
    emptyReplacingSerializer[ExprProto](_.exprInstance.isEmpty, ExprProto())

  val UnfSerializer =
    emptyReplacingSerializer[GUnforgeableProto](_.unfInstance.isEmpty, GUnforgeableProto())

  val ConnectiveSerializer =
    emptyReplacingSerializer[ConnectiveProto](_.connectiveInstance.isEmpty, ConnectiveProto())

  val NoneSerializer: DefaultSerializer[None.type] =
    emptyReplacingSerializer[None.type](_.isEmpty, None)

  val kryo = new Kryo()
  kryo.register(classOf[ParMapProto], ParMapSerializer)
  kryo.register(classOf[ParSetProto], ParSetSerializer)
  kryo.register(classOf[TaggedContinuationProto], TaggedContinuationSerializer)
  kryo.register(classOf[VarProto], VarSerializer)
  kryo.register(classOf[ExprProto], ExprSerializer)
  kryo.register(classOf[GUnforgeableProto], UnfSerializer)
  kryo.register(classOf[ConnectiveProto], ConnectiveSerializer)
  kryo.register(None.getClass, NoneSerializer)

  kryo.setRegistrationRequired(false)
  // Support deserialization of classes without no-arg constructors
  kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())

  def serializer[A](of: Class[A]): Serialize2ByteBuffer[A] = new Serialize2ByteBuffer[A] {

    private[this] val noSizeLimit = -1

    override def encode(gnat: A): ByteBuffer = {
      val output = new ByteBufferOutput(1024, noSizeLimit)
      kryo.writeObject(output, gnat)
      output.close()

      val buf = output.getByteBuffer
      buf.flip()
      buf
    }

    override def decode(bytes: ByteBuffer): A = {
      val input = new ByteBufferInput(bytes)
      val res   = kryo.readObject(input, of)
      input.close()
      res
    }

  }
}
