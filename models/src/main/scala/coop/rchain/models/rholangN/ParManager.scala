package coop.rchain.models.rholangN

import cats.effect.Sync
import com.google.protobuf.{CodedInputStream, CodedOutputStream}
import coop.rchain.rspace.hashing.Blake2b256Hash
import scodec.bits.ByteVector

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream, OutputStream}
import java.lang.Math.floorDiv
import scala.collection.immutable.BitSet
import coop.rchain.catscontrib.effect.implicits.sEval
import cats.implicits._

object ParManager {
  type M[T] = cats.Eval[T]

  def parToBytes(p: Par): ByteVector = {
    val baos = new ByteArrayOutputStream(p.serializedSize)
    Codecs.serialize[M](p, baos).value
    ByteVector(baos.toByteArray)
  }

  def parFromBytes(bv: ByteVector): Par = {
    val bais = new ByteArrayInputStream(bv.toArray)
    Codecs.deserialize[M](bais).value
  }

  def equals(self: Par, other: Any): Boolean = other match {
    case x: Par => x.rhoHash == self.rhoHash
    case _      => false
  }

  object Constructor {
    import SerializedSize._
    import RhoHash._
    import LocallyFree._
    import ConnectiveUsed._
    import EvalRequired._
    import SubstituteRequired._

    def createParProc(ps: Seq[Par]): ParProc = createParProc(SortedParSeq(ps))

    def createParProc(sortedPs: SortedParSeq): ParProc = {
      val size = sizeParProc(sortedPs)
      val hash = hashParProc(sortedPs)
      val lf   = locallyFreeParProc(sortedPs)
      val cu   = connectiveUsedParProc(sortedPs)
      val er   = evalRequiredParProc(sortedPs)
      val sr   = substituteRequiredParProc(sortedPs)
      val meta = new ParMetaData(size, hash, lf, cu, er, sr)
      new ParProc(sortedPs, meta)
    }

    def createGNil: GNil = {
      val size = sizeGNil()
      val hash = hashGNil()
      val lf   = locallyFreeGNil()
      val cu   = connectiveUsedGNil()
      val er   = evalRequiredGNil()
      val sr   = substituteRequiredGNil()
      val meta = new ParMetaData(size, hash, lf, cu, er, sr)
      new GNil(meta)
    }

    def createGInt(v: Long): GInt = {
      val size = sizeGInt(v)
      val hash = hashGInt(v)
      val lf   = locallyFreeGInt(v)
      val cu   = connectiveUsedGInt(v)
      val er   = evalRequiredGInt(v)
      val sr   = substituteRequiredGInt(v)
      val meta = new ParMetaData(size, hash, lf, cu, er, sr)
      new GInt(v, meta)
    }

    def createEList(ps: Seq[Par]): EList = {
      val size = sizeEList(ps)
      val hash = hashEList(ps)
      val lf   = locallyFreeEList(ps)
      val cu   = connectiveUsedEList(ps)
      val er   = evalRequiredEList(ps)
      val sr   = substituteRequiredEList(ps)
      val meta = new ParMetaData(size, hash, lf, cu, er, sr)
      new EList(ps, meta)
    }

    def createSend(chan: Par, data: Seq[Par], persistent: Boolean): Send = {
      val sortedData = SortedParSeq(data)
      val size       = sizeSend(chan, sortedData, persistent)
      val hash       = hashSend(chan, sortedData, persistent)
      val lf         = locallyFreeSend(chan, sortedData, persistent)
      val cu         = connectiveUsedSend(chan, sortedData, persistent)
      val er         = evalRequiredSend(chan, sortedData, persistent)
      val sr         = substituteRequiredSend(chan, sortedData, persistent)
      val meta       = new ParMetaData(size, hash, lf, cu, er, sr)
      new Send(chan, sortedData, persistent, meta)
    }
  }

  private object Constants {
    final val longSize    = 8
    final val booleanSize = 1
    final val hashSize    = Blake2b256Hash.length

    final val tagSize       = 1
    final val PARPROC: Byte = 0x01.toByte
    final val GNIL: Byte    = 0x02.toByte
    final val GINT: Byte    = 0x04.toByte
    final val ELIST: Byte   = 0x08.toByte
    final val SEND: Byte    = 0x80.toByte
  }

  private object RhoHash {
    import Constants._
    import java.util.concurrent.atomic.AtomicInteger

    private class Hashable(val tag: Byte, val bodySize: Int) {

      private val arrSize: Int     = bodySize + tagSize
      private val arr: Array[Byte] = new Array[Byte](arrSize)
      private val pos              = new AtomicInteger(tagSize)

      arr(0) = tag // Fill the first element of arr with the tag

      def appendByte(b: Byte): Unit = {
        val currentPos = pos.getAndIncrement()
        assert(currentPos + 1 <= arrSize, "Array size exceeded")
        arr(currentPos) = b
      }

      def appendBytes(bytes: Array[Byte]): Unit = {
        val bytesLength = bytes.length
        val currentPos  = pos.getAndAdd(bytesLength)
        assert(currentPos + bytesLength <= arrSize, "Array size exceeded")
        Array.copy(bytes, 0, arr, currentPos, bytesLength)
      }

      def appendParHash(p: Par): Unit = appendBytes(p.rhoHash.bytes.toArray)

      // Get the hash of the current array
      def calcHash: Blake2b256Hash = {
        val curSize = pos.get()

        if (curSize <= hashSize) {
          if (curSize == hashSize) {
            Blake2b256Hash.fromByteArray(arr)
          } else {
            val newBytes     = new Array[Byte](hashSize)
            val dataStartPos = hashSize - curSize

            for (i <- 0 until hashSize) {
              if (i < dataStartPos) newBytes(i) = 0x00.toByte // fill empty place with 0x00.toByte
              else newBytes(i) = arr(i - dataStartPos)
            }
            Blake2b256Hash.fromByteArray(newBytes)
          }
        } else {
          val hashData = arr.slice(0, curSize)
          Blake2b256Hash.create(hashData)
        }
      }
    }

    private object Hashable {
      def apply(tag: Byte, size: Int): Hashable = new Hashable(tag, size)
    }

    def hashParProc(ps: SortedParSeq): Blake2b256Hash = {
      val bodySize = hashSize * ps.size
      val hashable = Hashable(PARPROC, bodySize)
      ps.foreach(hashable.appendParHash)
      hashable.calcHash
    }

    def hashGNil(): Blake2b256Hash = Hashable(GNIL, 0).calcHash

    def hashGInt(v: Long): Blake2b256Hash = {
      def longToBytes(value: Long): Array[Byte] = {
        val byteArray = new Array[Byte](longSize)
        for (i <- 0 until longSize) {
          byteArray(7 - i) = ((value >>> (i * longSize)) & 0xFF).toByte
        }
        byteArray
      }
      val hashable = Hashable(GINT, longSize)
      hashable.appendBytes(longToBytes(v))
      hashable.calcHash
    }

    def hashEList(ps: Seq[Par]): Blake2b256Hash = {
      val bodySize = hashSize * ps.size
      val hashable = Hashable(ELIST, bodySize)
      ps.foreach(hashable.appendParHash)
      hashable.calcHash
    }

    def hashSend(chan: Par, data: SortedParSeq, persistent: Boolean): Blake2b256Hash = {
      def booleanToByte(v: Boolean): Byte = if (v) 1 else 0

      val bodySize = hashSize * (data.size + 1) + booleanSize
      val hashable = Hashable(SEND, bodySize)
      hashable.appendParHash(chan)
      data.foreach(hashable.appendParHash)
      hashable.appendByte(booleanToByte(persistent))
      hashable.calcHash
    }
  }

  private object SerializedSize {
    import Constants._

    private def sizeTag(): Int              = tagSize
    private def sizeLength(value: Int): Int = CodedOutputStream.computeUInt32SizeNoTag(value)
    private def sizeLong(value: Long): Int  = CodedOutputStream.computeInt64SizeNoTag(value)
    private def sizeBool(): Int             = 1
    private def sizePar(p: Par): Int        = p.serializedSize
    private def sizePars(ps: Seq[Par]): Int = ps.map(sizePar).sum

    def sizeParProc(ps: SortedParSeq): Int = {
      val tagSize    = sizeTag()
      val lengthSize = sizeLength(ps.size)
      val psSize     = sizePars(ps.toSeq)
      tagSize + lengthSize + psSize
    }

    def sizeGNil(): Int = sizeTag()

    def sizeGInt(v: Long): Int = sizeTag() + sizeLong(v)

    def sizeEList(ps: Seq[Par]): Int = {
      val tagSize    = sizeTag()
      val lengthSize = sizeLength(ps.size)
      val psSize     = sizePars(ps)
      tagSize + lengthSize + psSize
    }

    def sizeSend(chan: Par, data: SortedParSeq, persistent: Boolean): Int = {
      val tagSize        = sizeTag()
      val chanSize       = sizePar(chan)
      val dataLengthSize = sizeLength(data.size)
      val dataSize       = sizePars(data.toSeq)
      val persistentSize = sizeBool()
      tagSize + chanSize + dataLengthSize + dataSize + persistentSize
    }
  }

  private object LocallyFree {
    private def locallyFreeParSeq(ps: Seq[Par]) =
      ps.foldLeft(BitSet())((acc, p) => acc | p.locallyFree)

    def locallyFreeParProc(ps: SortedParSeq): BitSet = locallyFreeParSeq(ps.toSeq)

    def locallyFreeGNil(): BitSet = BitSet()

    def locallyFreeGInt(v: Long): BitSet = BitSet()

    def locallyFreeEList(ps: Seq[Par]): BitSet = locallyFreeParSeq(ps)

    def locallyFreeSend(chan: Par, data: SortedParSeq, persistent: Boolean): BitSet =
      chan.locallyFree | locallyFreeParSeq(data.toSeq)
  }

  private object ConnectiveUsed {
    private def cUsedParSeq(ps: Seq[Par]) =
      ps.exists(_.connectiveUsed)

    def connectiveUsedParProc(ps: SortedParSeq): Boolean = cUsedParSeq(ps.toSeq)

    def connectiveUsedGNil(): Boolean = false

    def connectiveUsedGInt(v: Long): Boolean = false

    def connectiveUsedEList(ps: Seq[Par]): Boolean = cUsedParSeq(ps)

    def connectiveUsedSend(chan: Par, data: SortedParSeq, persistent: Boolean): Boolean =
      chan.connectiveUsed || cUsedParSeq(data.toSeq)
  }

  private object EvalRequired {
    private def eRequiredParSeq(ps: Seq[Par]) =
      ps.exists(_.evalRequired)

    def evalRequiredParProc(ps: SortedParSeq): Boolean = eRequiredParSeq(ps.toSeq)

    def evalRequiredGNil(): Boolean = false

    def evalRequiredGInt(v: Long): Boolean = false

    def evalRequiredEList(ps: Seq[Par]): Boolean = eRequiredParSeq(ps)

    def evalRequiredSend(chan: Par, data: SortedParSeq, persistent: Boolean): Boolean =
      eRequiredParSeq(data.toSeq)
  }

  private object SubstituteRequired {
    private def sRequiredParSeq(ps: Seq[Par]) =
      ps.exists(_.substituteRequired)

    def substituteRequiredParProc(ps: SortedParSeq): Boolean = sRequiredParSeq(ps.toSeq)

    def substituteRequiredGNil(): Boolean = false

    def substituteRequiredGInt(v: Long): Boolean = false

    def substituteRequiredEList(ps: Seq[Par]): Boolean = sRequiredParSeq(ps)

    def substituteRequiredSend(chan: Par, data: SortedParSeq, persistent: Boolean): Boolean =
      sRequiredParSeq(data.toSeq)
  }

  private object Codecs {
    import Constants._

    def serialize[F[_]: Sync](par: Par, output: OutputStream): F[Unit] = {
      val cos = CodedOutputStream.newInstance(output)

      def writeTag(x: Byte): F[Unit]       = Sync[F].delay(cos.writeRawByte(x))
      def writeLength(x: Int): F[Unit]     = Sync[F].delay(cos.writeUInt32NoTag(x))
      def writeLong(x: Long): F[Unit]      = Sync[F].delay(cos.writeInt64NoTag(x))
      def writeBool(x: Boolean): F[Unit]   = Sync[F].delay(cos.writeBoolNoTag(x))
      def writePar(p: Par): F[Unit]        = Sync[F].defer(loop(p))
      def writePars(ps: Seq[Par]): F[Unit] = ps.traverse_(loop)

      def loop(p: Par): F[Unit] =
        p match {
          case parProc: ParProc =>
            for {
              _ <- writeTag(PARPROC)
              _ <- writeLength(parProc.ps.size)
              _ <- writePars(parProc.ps.toSeq)
            } yield ()

          case _: GNil =>
            writeTag(GNIL)

          case gInt: GInt =>
            for {
              _ <- writeTag(GINT)
              _ <- writeLong(gInt.v)
            } yield ()

          case eList: EList =>
            for {
              _ <- writeTag(ELIST)
              _ <- writeLength(eList.ps.size)
              _ <- writePars(eList.ps)
            } yield ()

          case send: Send =>
            for {
              _ <- writeTag(SEND)
              _ <- writePar(send.chan)
              _ <- writeLength(send.data.size)
              _ <- writePars(send.data.toSeq)
              _ <- writeBool(send.persistent)
            } yield ()

        }
      writePar(par) *> Sync[F].delay(cos.flush())
    }

    def deserialize[F[_]: Sync](input: InputStream): F[Par] = {
      val cis = CodedInputStream.newInstance(input)

      def readTag(): F[Byte]                 = Sync[F].delay(cis.readRawByte())
      def readLength(): F[Int]               = Sync[F].delay(cis.readUInt32())
      def readLong(): F[Long]                = Sync[F].delay(cis.readInt64())
      def readBool(): F[Boolean]             = Sync[F].delay(cis.readBool())
      def readPar(): F[Par]                  = Sync[F].defer(loop())
      def readPars(count: Int): F[List[Par]] = (1 to count).toList.traverse(_ => readPar())

      def loop(): F[Par] =
        for {
          tag <- readTag()
          par <- tag match {
                  case PARPROC =>
                    for {
                      count <- readLength()
                      ps    <- readPars(count)
                    } yield ParProc(ps)

                  case GNIL =>
                    Sync[F].pure(GNil())

                  case GINT =>
                    readLong().map(GInt(_))

                  case ELIST =>
                    for {
                      count <- readLength()
                      ps    <- readPars(count)
                    } yield EList(ps)

                  case SEND =>
                    for {
                      chan       <- readPar()
                      dataSize   <- readLength()
                      dataSeq    <- readPars(dataSize)
                      persistent <- readBool()
                    } yield Send(chan, dataSeq, persistent)

                  case _ =>
                    Sync[F].raiseError(
                      new IllegalArgumentException("Invalid tag for Par deserialization")
                    )
                }
        } yield par
      readPar()
    }
  }
}
