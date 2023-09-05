package coop.rchain.models.rholangn.parmanager

import coop.rchain.models.rholangn._

import scala.util.control.TailCalls._

object EvalRequired {
  def eReqSeq(ps: Seq[RhoTypeN]): TailRec[Boolean] =
    ps match {
      case head +: tail =>
        eReq(head).flatMap {
          case true  => done(true)
          case false => tailcall(eReqSeq(tail))
        }
      case _ => done(false)
    }

  def eReqKVPair(kv: (RhoTypeN, RhoTypeN)): TailRec[Boolean] =
    eReq(kv._1).flatMap {
      case true  => done(true)
      case false => eReq(kv._2)
    }
  def eReqKVPairs(kVPairs: Seq[(RhoTypeN, RhoTypeN)]): TailRec[Boolean] =
    kVPairs match {
      case head +: tail =>
        eReqKVPair(head).flatMap {
          case true  => done(true)
          case false => tailcall(eReqKVPairs(tail))
        }
      case _ => done(false)
    }

  def eReq(p: RhoTypeN): TailRec[Boolean] = tailcall(evalRequiredFn(p))

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def evalRequiredFn(input: RhoTypeN): TailRec[Boolean] = input match {

    /** Basic types */
    case p: BasicN =>
      p match {
        case _: NilN.type    => done(false)
        case pProc: ParProcN => tailcall(eReqSeq(pProc.ps))
        case _               => done(true)
      }

    /** Ground types */
    case _: GroundN => done(false)

    /** Collections */
    case p: EListN  => tailcall(eReqSeq(p.ps))
    case p: ETupleN => tailcall(eReqSeq(p.ps))
    case p: ESetN   => tailcall(eReqSeq(p.ps.toSeq))
    case p: EMapN   => tailcall(eReqKVPairs(p.ps.toSeq))

    /** Vars */
    case _: VarN => done(true)

    /** Operations */
    case _: OperationN => done(true)

    /** Unforgeable names */
    case _: UnforgeableN => done(false)

    /** Connective */
    case _: ConnectiveN => done(false)

    /** Other types */
    case p: BundleN => tailcall(eReq(p.body))

    case p => throw new Exception(s"Undefined type $p")
  }
}
