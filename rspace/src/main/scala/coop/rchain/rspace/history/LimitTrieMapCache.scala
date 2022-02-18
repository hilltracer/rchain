/*
  Draft variant of single cache (for reading) for several RadixHistory instances
  TODO: thread safety, increase speed
 */
package coop.rchain.rspace.history
import scala.collection.concurrent.TrieMap
import java.util.concurrent.ConcurrentLinkedQueue

object LimitTrieMapCache {
  def apply[A, B](maxSize: Int): LimitTrieMapCache[A, B] = new LimitTrieMapCache[A, B](maxSize)
}

class LimitTrieMapCache[A, B](maxSize: Int) {
  private val cache: TrieMap[A, B]            = TrieMap.empty
  private val queue: ConcurrentLinkedQueue[A] = new ConcurrentLinkedQueue()

  private def addKeyToQueue(key: A): Unit = {
    { val _ = queue.remove(key) }
    { val _ = queue.add(key) }
    (1 to (queue.size - maxSize)).foreach(_ => cache.remove(queue.remove()))
  }

  def get(key: A): Option[B] = {
    addKeyToQueue(key)
    cache.get(key)
  }

  def update(key: A, value: B): Unit = {
    addKeyToQueue(key)
    cache.update(key, value)
  }
}
