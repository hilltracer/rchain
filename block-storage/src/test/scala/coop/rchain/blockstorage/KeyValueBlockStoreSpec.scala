package coop.rchain.blockstorage

import cats.effect.Sync
import cats.instances.tuple._
import cats.syntax.all._
import com.google.protobuf.ByteString
import coop.rchain.casper.protocol.{ApprovedBlock, ApprovedBlockCandidate, BlockMessage}
import coop.rchain.models.blockImplicits.{blockElementGen, blockElementsGen}
import coop.rchain.shared.ByteStringOps.RichByteString
import coop.rchain.store.KeyValueStore
import monix.eval.Task
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import java.nio.ByteBuffer

class KeyValueBlockStoreSpec extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  class KV[F[_]: Sync](
      getResult: Option[ByteString],
      iterateMap: Map[ByteString, ByteString] = Map()
  ) extends KeyValueStore[F] {
    import scala.collection.mutable

    val inputKeys = mutable.MutableList[ByteString]()

    val inputPut = mutable.MutableList[ByteString]()

    override def get[T](keys: Seq[ByteBuffer], fromBuffer: ByteBuffer => T): F[Seq[Option[T]]] =
      Sync[F].delay {
        inputKeys ++= keys.map(ByteString.copyFrom)

        Seq(getResult.map(_.asReadOnlyByteBuffer).map(fromBuffer))
      }

    override def put[T](kvPairs: Seq[(ByteBuffer, T)], toBuffer: T => ByteBuffer): F[Unit] =
      Sync[F].delay {
        inputKeys ++= kvPairs.map(_._1).map(ByteString.copyFrom)

        inputPut ++= kvPairs.map(_.map(toBuffer andThen ByteString.copyFrom)).map(_._2)
      }

    override def iterate[T](f: Iterator[(ByteBuffer, ByteBuffer)] => T): F[T] = Sync[F].delay {
      val iterBuffer = iterateMap.map {
        case (k, v) => (k.toDirectByteBuffer, v.toDirectByteBuffer)
      }
      f(iterBuffer.iterator)
    }

    // Delete should not be used, block store can only add data.
    override def delete(keys: Seq[ByteBuffer]): F[Int] = ???
  }

  def notImplementedKV[F[_]]: KeyValueStore[F] = new KeyValueStore[F] {
    override def get[T](keys: Seq[ByteBuffer], fromBuffer: ByteBuffer => T): F[Seq[Option[T]]] = ???
    override def put[T](kvPairs: Seq[(ByteBuffer, T)], toBuffer: T => ByteBuffer): F[Unit]     = ???
    override def delete(keys: Seq[ByteBuffer]): F[Int]                                         = ???
    override def iterate[T](f: Iterator[(ByteBuffer, ByteBuffer)] => T): F[T]                  = ???
  }

  implicit val scheduler = monix.execution.Scheduler.global

  import KeyValueBlockStore._

  val blockProtoToByteString = blockProtoToBytes _ andThen ByteString.copyFrom

  /**
    * Block store tests.
    */
  "Block store" should "get data from underlying key-value store" in {
    forAll(blockElementGen(), arbitrary[String]) { (block, keyString) =>
      // Testing block serialized
      val blockBytes = blockProtoToByteString(block.toProto)

      // Underlying key-value store
      val kv = new KV[Task](blockBytes.some)

      // Block store under testing
      val bs = new KeyValueBlockStore(kv, notImplementedKV)

      // Key to request from block store
      val key = ByteString.copyFromUtf8(keyString)

      // Test block store GET operation
      val result = bs.get(key).runSyncUnsafe()

      // Requested key should match
      kv.inputKeys shouldBe Seq(key)

      // Result should be testing block
      result shouldBe block.some
    }
  }

  it should "not get data if not exists in underlying key-value store" in {
    forAll(arbitrary[String]) { keyString =>
      // Underlying key-value store
      val kv = new KV[Task](none)

      // Block store under testing
      val bs = new KeyValueBlockStore(kv, notImplementedKV)

      // Key to request from block store
      val key = ByteString.copyFromUtf8(keyString)

      // Test block store GET operation
      val result = bs.get(key).runSyncUnsafe()

      // Result should be none
      result shouldBe none
    }
  }

  it should "put data to underlying key-value store" in {
    forAll(blockElementGen()) { block =>
      // Testing block serialized
      val blockBytes = blockProtoToByteString(block.toProto)

      // Underlying key-value store
      val kv = new KV[Task](blockBytes.some)

      // Block store under testing
      val bs = new KeyValueBlockStore(kv, notImplementedKV)

      // Test block store PUT operation
      bs.put(block).runSyncUnsafe()

      // Block hash should match the key
      kv.inputKeys shouldBe Seq(block.blockHash)

      // Result should be testing block
      kv.inputPut shouldBe Seq(blockBytes)
    }
  }

  it should "iterate over data in underlying key-value store" in {
    forAll(blockElementsGen, sizeRange(25)) { blocks =>
      def toBufferKV(block: BlockMessage) = {
        val key   = block.blockHash
        val value = ByteString.copyFrom(blockProtoToBytes(block.toProto))
        (key, (value, block))
      }

      // Testing state
      val blocksBytesMap: Map[ByteString, (ByteString, BlockMessage)] = blocks.map(toBufferKV).toMap
      val bytesMap: Map[ByteString, ByteString]                       = blocksBytesMap.map(_.map(_._1))
      val blocksMap: Map[ByteString, BlockMessage]                    = blocksBytesMap.map(_.map(_._2))

      // Underlying key-value store
      val kv = new KV[Task](none, bytesMap)

      // Block store under testing
      val bs = new KeyValueBlockStore(kv, notImplementedKV)

      if (blocks.nonEmpty) {
        // Pick one key to search for
        val searchKey = blocksMap.head._1

        // Find block with that key
        val result = bs.find(_ == searchKey).runSyncUnsafe()

        // Result should be key/block pair
        result shouldBe Seq((searchKey, blocksMap(searchKey)))

        // Find no blocks for non existing key
        val resultEmpty = bs.find(_ == ByteString.EMPTY).runSyncUnsafe()

        // Result should be empty
        resultEmpty shouldBe Seq.empty

        // Find multiple blocks
        val resultMulti = bs.find(blocksBytesMap.keySet(_)).runSyncUnsafe()

        // Result should be all testing blocks
        resultMulti.toSet shouldBe blocksMap.toSet
      } else {
        // Find no blocks for empty store
        val result = bs.find(_ == ByteString.EMPTY).runSyncUnsafe()

        // Result should be empty
        result shouldBe Seq.empty
      }
    }
  }

  /**
    * Approved block store
    */
  def toApprovedBlock(block: BlockMessage): ApprovedBlock = {
    val candidate = ApprovedBlockCandidate(block, requiredSigs = 0)
    ApprovedBlock(candidate, sigs = List())
  }

  it should "get approved block from underlying key-value store" in {
    forAll(blockElementGen()) { block =>
      // Testing block serialized
      val approvedBlock   = toApprovedBlock(block)
      val blockByteBuffer = approvedBlock.toProto.toByteString

      // Underlying key-value store
      val kv = new KV[Task](blockByteBuffer.some)

      // Block store under testing
      val bs = new KeyValueBlockStore(notImplementedKV, kv)

      // Test block store GET approved block operation
      val result = bs.getApprovedBlock.runSyncUnsafe()

      // Requested key should match
      kv.inputKeys shouldBe Seq(ByteString.copyFrom(approvedBlockKey))

      // Result should be testing block
      result shouldBe approvedBlock.some
    }
  }

  it should "not get approved block if not exists in underlying key-value store" in {
    // Underlying key-value store
    val kv = new KV[Task](none)

    // Block store under testing
    val bs = new KeyValueBlockStore(notImplementedKV, kv)

    // Test block store GET approved block operation
    val result = bs.getApprovedBlock.runSyncUnsafe()

    // Result should be none
    result shouldBe none
  }

  it should "put approved block to underlying key-value store" in {
    forAll(blockElementGen()) { block =>
      // Testing block serialized
      val approvedBlock = toApprovedBlock(block)
      val blockBytes    = approvedBlock.toProto.toByteString

      // Underlying key-value store
      val kv = new KV[Task](blockBytes.some)

      // Block store under testing
      val bs = new KeyValueBlockStore(notImplementedKV, kv)

      // Test block store PUT approved block operation
      bs.putApprovedBlock(approvedBlock).runSyncUnsafe()

      // Block hash should match the key
      kv.inputKeys shouldBe Seq(ByteString.copyFrom(approvedBlockKey))

      // Result should be testing block
      kv.inputPut shouldBe Seq(blockBytes)
    }
  }

}
