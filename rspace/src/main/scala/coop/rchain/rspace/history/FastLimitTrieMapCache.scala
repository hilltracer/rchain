package coop.rchain.rspace.history

import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap

/*
  Draft variant of single cache (for reading) for several RadixHistory instances
  TODO: thread safety, increase speed
 */


/*
  * maxSize   - values count after which old records should be cleared
  * cache     - TrieMap[key, (value, Option[nextKey], Option[prevKey])]; nextKey closer to topKey; prevKey closer to bottomKey;
  * topKey    - last read item's key
  * bottomKey - most old item's key
  */
class FastLimitTrieMapCache[A, B](
  maxSize: Int,
  cache: TrieMap[A, (B, Option[A], Option[A])] = TrieMap.empty[A, (B, Option[A], Option[A])],
  var topKey: Option[A] = None, var bottomKey: Option[A] = None) {
  def get(key: A): Option[B] = {
    val optionValue = cache.get(key)

    if (optionValue.isEmpty)
      None
    else {
      if (topKey.get != key) {
        if (bottomKey.get == key) {
          val (lastValue, nextKey, _) = cache(bottomKey.get)
          cache(bottomKey.get) = (lastValue, nextKey, None)
          val (firstValue, _, prevKey) = cache(topKey.get)
          cache(topKey.get) = (firstValue, Some(key), prevKey)
          topKey = Some(key)
          bottomKey = cache(key)._2
        }
        else {
          val (nextValue, nextKey, _) = cache(cache(key)._2.get)
          cache(cache(key)._2.get) = (nextValue, nextKey, cache(key)._3)
          val (prevValue, _, prevKey) = cache(cache(key)._3.get)
          cache(cache(key)._3.get) = (prevValue, cache(key)._2, prevKey)
          topKey = Some(key)
        }
      }
      Some(optionValue.get._1)
    }
  }

  def update(key: A, value: B): Unit = {
    val optionValue = cache.get(key)

    if (optionValue.isDefined) {
      val (_, nextKey, prevKey) = cache(key)
      cache(key) = (value, nextKey, prevKey)
      get(key)
    }
    else {
      cache(key) = (value, None, topKey)
      if (topKey.isEmpty) {
        topKey = Some(key)
        bottomKey = Some(key)
      }
      else {
        val nextBottomKey = clearOldItems()
        val (value, _, prevKey) = cache(topKey.get)
        cache(topKey.get) = (value, Some(key), prevKey)
        topKey = Some(key)
        bottomKey = nextBottomKey
      }
    }
  }

  @tailrec
  private def prepareOldItems(oldItemsCount: Int, bottomKey: Option[A],
                              currentOldItemsList: List[A]): (List[A], Option[A]) = {
    if (bottomKey.isEmpty)
      (currentOldItemsList, bottomKey)
    else {
      val nextBottomKey = cache(bottomKey.get)._2
      prepareOldItems(oldItemsCount - 1, nextBottomKey, bottomKey.get::currentOldItemsList)
    }
  }

  private def clearOldItems(): Option[A] = {
    if (maxSize < cache.size) {
      val (oldItems, nextBottomKey) = prepareOldItems(maxSize/3, bottomKey, Nil)
      oldItems.foreach(cache.remove)
      nextBottomKey
    }
    else
      bottomKey
  }
}
