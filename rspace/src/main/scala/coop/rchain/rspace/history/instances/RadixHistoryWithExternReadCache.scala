package coop.rchain.rspace.history.instances

import cats.Parallel
import cats.effect.Sync
import cats.syntax.all._
import coop.rchain.rspace.hashing.Blake2b256Hash
import coop.rchain.rspace.history.RadixTreeWithExternReadCache._
import coop.rchain.rspace.history._
import coop.rchain.shared.syntax.sharedSyntaxKeyValueStore
import coop.rchain.store.{KeyValueStore, KeyValueTypedStore}
import scodec.bits.ByteVector

/**
  * History implementation with radix tree
  */
object RadixHistoryWithExternReadCache {
  val emptyRootHash: Blake2b256Hash = Blake2b256Hash.fromByteVector(hashNode(emptyNode)._1)

  /**
   * Cache for storing read and decoded nodes.
   * In cache load kv-pair (hash, node).
   * Where hash  - Blake2b256Hash of serializing nodes data,
   *       node - deserialized data of this node.
   */
  private val maxCacheRecordCount = 1000000
  val cache: LimitTrieMapCache[ByteVector, Node] =
    new LimitTrieMapCache[ByteVector, Node](maxCacheRecordCount)

  def apply[F[_]: Sync: Parallel](
      root: Blake2b256Hash,
      store: KeyValueTypedStore[F, ByteVector, ByteVector]
  ): F[RadixHistoryWithExternReadCache[F]] =
    for {
      impl <- Sync[F].delay(new RadixTreeImpl[F](store, cache))
      node <- impl.loadNode(root.bytes, noAssert = true)
    } yield RadixHistoryWithExternReadCache(root, node, impl, store, cache)

  def createStore[F[_]: Sync](
      store: KeyValueStore[F]
  ): KeyValueTypedStore[F, ByteVector, ByteVector] =
    store.toTypedStore(scodec.codecs.bytes, scodec.codecs.bytes)
}

final case class RadixHistoryWithExternReadCache[F[_]: Sync: Parallel](
    rootHash: Blake2b256Hash,
    rootNode: Node,
    impl: RadixTreeImpl[F],
    store: KeyValueTypedStore[F, ByteVector, ByteVector],
    cache: LimitTrieMapCache[ByteVector, Node]
) extends History[F] {
  override type HistoryF = History[F]

  override def root: Blake2b256Hash = rootHash

  override def reset(root: Blake2b256Hash): F[History[F]] =
    for {
      impl <- Sync[F].delay(new RadixTreeImpl[F](store, cache))
      node <- impl.loadNode(root.bytes, noAssert = true)
    } yield this.copy(root, node, impl, store)

  override def read(key: ByteVector): F[Option[ByteVector]] =
    impl.read(rootNode, key)

  override def process(actions: List[HistoryAction]): F[History[F]] =
    for {
      // TODO: To improve time perfomance, it is possible to implement this check into the makeActions().
      _ <- new RuntimeException("Cannot process duplicate actions on one key.").raiseError
            .unlessA(hasNoDuplicates(actions))

      newRootNodeOpt <- impl.makeActions(rootNode, actions)
      newRootHash <- newRootNodeOpt.traverse { newRootNode =>
                      val hash = impl.saveNode(newRootNode)
                      impl.commit().as(hash)
                    }
      _ <- Sync[F].delay(impl.clearWriteCache())
    } yield
      if (newRootHash.isDefined)
        this.copy(
          Blake2b256Hash.fromByteVector(newRootHash.get),
          newRootNodeOpt.get,
          impl,
          store
        )
      else this

  private def hasNoDuplicates(actions: List[HistoryAction]) =
    actions.map(_.key).distinct.size == actions.size
}
