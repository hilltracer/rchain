package coop.rchain.models.rholangN

import cats.effect.Sync
import cats.implicits._
import scodec.bits.ByteVector

import java.lang.Math.floorDiv

object PM {
  type M[T] = monix.eval.Coeval[T]

  def createPar(
      ps: Seq[ParInst],
      bytesOpt: Option[ByteVector] = None,
      hashOpt: Option[ByteVector] = None
  ): Par = ???

  def createSend(
      chan: Par,
      data: Seq[Par],
      persistent: Boolean,
      hashOpt: Option[ByteVector] = None
  ): GInt = ???

  def createGInt(
      v: Long,
      bytesOpt: Option[ByteVector] = None,
      hashOpt: Option[ByteVector] = None
  ): GInt = ???

  def createEList(
      ps: Seq[Par],
      bytesOpt: Option[ByteVector] = None,
      hashOpt: Option[ByteVector] = None
  ): EList = ???

  def equals(self: ParInst, other: Any): Boolean = other match {
    case x: ParInst => x.rhoHash == self.rhoHash
    case _      => false
  }

  def parFromBytes(bv: ByteVector): Par = Codecs.decodePar[M](bv).value
  def parToBytes(p: Par): ByteVector    = Codecs.encodePar[M](p).value

  private object Utils {
    def traverseSeq[F[_]: Sync, T1, T2](seq: Seq[T1], f: T1 => F[T2]): F[List[T2]] =
      Sync[F].defer(seq.toList.traverse(f))

    def traverseVal[F[_]: Sync, T1, T2](rho: T1, f: T1 => F[T2]): F[T2] = Sync[F].defer(f(rho))
  }

  private object Codecs {
    import Utils._

    private val IdSize                 = 1
    def headerSize(bodySize: Int): Int = IdSize + floorDiv(bodySize, 128)

    def serializedSizePar(ps:  Seq[ParInst]): Int = {
      val bodySize = ps.map(_.serializedSize).sum
      headerSize(bodySize) + bodySize
    }

    def encodeSizeParInts[F[_]: Sync](p: ParInst): F[Int] = p match {
      case GInt(v, _) =>
        val bodySize = 8 // Long size
        Sync[F].pure(headerSize(bodySize) + bodySize)
      case EList(ps, _)                    => ???
      case Send(chan, data, persistent, _) => ???
    }

    def encodePar[F[_]: Sync](p: Par): F[ByteVector] = ???

    def decodePar[F[_]: Sync](bv: ByteVector): F[Par] = ???

    def encodeSend[F[_]: Sync](chan: Par, data: Seq[Par], persistent: Boolean): F[ByteVector] = ???
    def encodeGInt[F[_]: Sync](v: Long): F[ByteVector]                                        = ???
    def encodeEList[F[_]: Sync](ps: Seq[Par]): F[ByteVector]                                  = ???

    object Id {
      // For things that are truly optional
      final val PAR: Byte = 0

      // Ground types and collections
//      final val BOOL: Byte           = 1
      final val INT: Byte = 2
//      final val STRING: Byte         = 3
//      final val URI: Byte            = 4
//      final val PRIVATE: Byte        = 5
      final val ELIST: Byte = 6
//      final val ETUPLE: Byte         = 7
//      final val ESET: Byte           = 8
//      final val EMAP: Byte           = 9
//      final val DEPLOYER_AUTH: Byte  = 10
//      final val DEPLOY_ID: Byte      = 11
//      final val SYS_AUTH_TOKEN: Byte = 12
//      final val BIG_INT: Byte        = 13

      // Vars
//      final val BOUND_VAR: Byte = 20
//      final val FREE_VAR: Byte  = 21
//      final val WILDCARD: Byte  = 22
//      final val REMAINDER: Byte = 23

      // Expr
//      final val EVAR: Byte        = 30
//      final val ENEG: Byte        = 31
//      final val EMULT: Byte       = 32
//      final val EDIV: Byte        = 33
//      final val EPLUS: Byte       = 34
//      final val EMINUS: Byte      = 35
//      final val ELT: Byte         = 36
//      final val ELTE: Byte        = 37
//      final val EGT: Byte         = 38
//      final val EGTE: Byte        = 39
//      final val EEQ: Byte         = 40
//      final val ENEQ: Byte        = 41
//      final val ENOT: Byte        = 42
//      final val EAND: Byte        = 43
//      final val EOR: Byte         = 44
//      final val EMETHOD: Byte     = 45
//      final val EBYTEARR: Byte    = 46
//      final val EEVAL: Byte       = 47
//      final val EMATCHES: Byte    = 48
//      final val EPERCENT: Byte    = 49
//      final val EPLUSPLUS: Byte   = 50
//      final val EMINUSMINUS: Byte = 51
//      final val EMOD: Byte        = 52
//      final val ESHORTAND: Byte   = 53
//      final val ESHORTOR: Byte    = 54

      // Other
//      final val QUOTE: Byte    = 71
//      final val CHAN_VAR: Byte = 72

      final val SEND: Byte = 80
//      final val RECEIVE: Byte           = 81
//      final val NEW: Byte               = 82
//      final val MATCH: Byte             = 83
//      final val BUNDLE_EQUIV: Byte      = 84
//      final val BUNDLE_READ: Byte       = 85
//      final val BUNDLE_WRITE: Byte      = 86
//      final val BUNDLE_READ_WRITE: Byte = 87

//      final val CONNECTIVE_NOT: Byte       = 100
//      final val CONNECTIVE_AND: Byte       = 101
//      final val CONNECTIVE_OR: Byte        = 102
//      final val CONNECTIVE_VARREF: Byte    = 103
//      final val CONNECTIVE_BOOL: Byte      = 104
//      final val CONNECTIVE_INT: Byte       = 105
//      final val CONNECTIVE_STRING: Byte    = 106
//      final val CONNECTIVE_URI: Byte       = 107
//      final val CONNECTIVE_BYTEARRAY: Byte = 108
//      final val CONNECTIVE_BIG_INT: Byte   = 109
    }
  }
}

//import java.nio.ByteBuffer
//val buffer = ByteBuffer.allocate(8)
//buffer.putLong(v)
//val byteArray = buffer.array()
