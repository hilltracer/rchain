package coop.rchain.models.rholangN

import ParManager.Constructor._
import ParManager._

sealed trait Par {
  protected def meta: ParMetaData
  lazy val connectiveUsed: Boolean = connectiveUsedM.value
  val connectiveUsedM: M[Boolean] = meta.connectiveUsed.memoize
}
sealed trait Expr extends Par

class ParProc(val ps: Seq[Par], protected val meta: ParMetaData) extends Par {}
object ParProc { def apply(ps: Seq[Par]): ParProc = createParProc(ps) }

final class GInt(val v: Long, protected val meta: ParMetaData) extends Expr
object GInt { def apply(v: Long): GInt = createGInt(v) }

final class ParMetaData(val connectiveUsed: M[Boolean])