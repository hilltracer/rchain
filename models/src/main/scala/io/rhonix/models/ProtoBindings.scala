package io.rhonix.models

import cats.effect.Sync
import cats.implicits._
import io.rhonix.models.Connective.ConnectiveInstance._
import io.rhonix.models.Expr.ExprInstance._
import io.rhonix.models.GUnforgeable._
import io.rhonix.models.TaggedContinuation.TaggedCont
import io.rhonix.models.Var.VarInstance
import io.rhonix.models.protobuf.ExprProto.ExprInstance
import io.rhonix.models.protobuf._

import scala.language.higherKinds

object ProtoBindings {
  type M[T] = monix.eval.Coeval[T]

  def toProto(rhoType: ProtoConvertible): StacksafeMessage[_] = rhoType match {
    case x: Par                => toProto(x)
    case x: Send               => toProto(x)
    case x: Receive            => toProto(x)
    case x: New                => toProto(x)
    case x: Expr               => toProto(x)
    case x: Match              => toProto(x)
    case x: MatchCase          => toProto(x)
    case x: Bundle             => toProto(x)
    case x: GUnforgeable       => toProto(x)
    case x: GPrivate           => toProto(x)
    case x: GDeployId          => toProto(x)
    case x: GDeployerId        => toProto(x)
    case x: GSysAuthToken      => toProto(x)
    case x: Connective         => toProto(x)
    case x: ConnectiveBody     => toProto(x)
    case x: VarRef             => toProto(x)
    case x: ReceiveBind        => toProto(x)
    case x: Var                => toProto(x)
    case x: ENot               => toProto(x)
    case x: ENeg               => toProto(x)
    case x: EMult              => toProto(x)
    case x: EDiv               => toProto(x)
    case x: EPlus              => toProto(x)
    case x: EMinus             => toProto(x)
    case x: ELt                => toProto(x)
    case x: ELte               => toProto(x)
    case x: EGt                => toProto(x)
    case x: EGte               => toProto(x)
    case x: EEq                => toProto(x)
    case x: ENeq               => toProto(x)
    case x: EAnd               => toProto(x)
    case x: EOr                => toProto(x)
    case x: EVar               => toProto(x)
    case x: EList              => toProto(x)
    case x: ETuple             => toProto(x)
    case x: EMethod            => toProto(x)
    case x: EMatches           => toProto(x)
    case x: EPercentPercent    => toProto(x)
    case x: EPlusPlus          => toProto(x)
    case x: EMinusMinus        => toProto(x)
    case x: EMod               => toProto(x)
    case x: EShortAnd          => toProto(x)
    case x: EShortOr           => toProto(x)
    case x: TaggedContinuation => toProto(x)
    case x: ESet               => toProto(x)
    case x: EMap               => toProto(x)
    case x: ParWithRandom      => toProto(x)
    case x: PCost              => toProto(x)
    case x: ListParWithRandom  => toProto(x)
    case x: BindPattern        => toProto(x)
    case x: ListBindPatterns   => toProto(x)
    case x: KeyValuePair       => toProto(x)
    case x: DeployId           => toProto(x)
    case x: DeployerId         => toProto(x)
  }

  def fromProto(proto: StacksafeMessage[_]): ProtoConvertible = proto match {
    case x: ParProto                => fromProto(x)
    case x: SendProto               => fromProto(x)
    case x: ReceiveProto            => fromProto(x)
    case x: NewProto                => fromProto(x)
    case x: ExprProto               => fromProto(x)
    case x: MatchProto              => fromProto(x)
    case x: MatchCaseProto          => fromProto(x)
    case x: BundleProto             => fromProto(x)
    case x: GUnforgeableProto       => fromProto(x)
    case x: GPrivateProto           => fromProto(x)
    case x: GDeployIdProto          => fromProto(x)
    case x: GDeployerIdProto        => fromProto(x)
    case x: GSysAuthTokenProto      => fromProto(x)
    case x: ConnectiveProto         => fromProto(x)
    case x: ConnectiveBodyProto     => fromProto(x)
    case x: VarRefProto             => fromProto(x)
    case x: ReceiveBindProto        => fromProto(x)
    case x: VarProto                => fromProto(x)
    case x: ENotProto               => fromProto(x)
    case x: ENegProto               => fromProto(x)
    case x: EMultProto              => fromProto(x)
    case x: EDivProto               => fromProto(x)
    case x: EPlusProto              => fromProto(x)
    case x: EMinusProto             => fromProto(x)
    case x: ELtProto                => fromProto(x)
    case x: ELteProto               => fromProto(x)
    case x: EGtProto                => fromProto(x)
    case x: EGteProto               => fromProto(x)
    case x: EEqProto                => fromProto(x)
    case x: ENeqProto               => fromProto(x)
    case x: EAndProto               => fromProto(x)
    case x: EOrProto                => fromProto(x)
    case x: EVarProto               => fromProto(x)
    case x: EListProto              => fromProto(x)
    case x: ETupleProto             => fromProto(x)
    case x: EMethodProto            => fromProto(x)
    case x: EMatchesProto           => fromProto(x)
    case x: EPercentPercentProto    => fromProto(x)
    case x: EPlusPlusProto          => fromProto(x)
    case x: EMinusMinusProto        => fromProto(x)
    case x: EModProto               => fromProto(x)
    case x: EShortAndProto          => fromProto(x)
    case x: EShortOrProto           => fromProto(x)
    case x: TaggedContinuationProto => fromProto(x)
    case x: ESetProto               => fromProto(x)
    case x: EMapProto               => fromProto(x)
    case x: ParWithRandomProto      => fromProto(x)
    case x: PCostProto              => fromProto(x)
    case x: ListParWithRandomProto  => fromProto(x)
    case x: BindPatternProto        => fromProto(x)
    case x: ListBindPatternsProto   => fromProto(x)
    case x: KeyValuePairProto       => fromProto(x)
    case x: DeployIdProto           => fromProto(x)
    case x: DeployerIdProto         => fromProto(x)
  }

  def traverse[F[_]: Sync, T1, T2](seq: Seq[T1], f: T1 => F[T2]): F[List[T2]] =
    Sync[F].defer(seq.toList.traverse(f))
  def traverseMapList[F[_]: Sync, T1, T2](map: List[(T1, T1)], f: T1 => F[T2]): F[List[(T2, T2)]] =
    Sync[F].defer(map.traverse {
      case (par1, par2) =>
        for {
          par1_ <- f(par1)
          par2_ <- f(par2)
        } yield (par1_, par2_)
    })
  def traverseMapKeyString[F[_]: Sync, T1Key, T1, T2](
      map: Map[T1Key, T1],
      f: T1 => F[T2]
  ): F[Map[T1Key, T2]] =
    Sync[F].defer(
      map.toList
        .traverse { case (str, par) => f(par).map((str, _)) }
        .map(_.toMap)
    )
  def evaluate[F[_]: Sync, T1, T2](rho: T1, f: T1 => F[T2]): F[T2] = Sync[F].defer(f(rho))

  def toProto(x: Par): ParProto = toProtoSPar[M](x).value
  private def toProtoSPar[F[_]: Sync](x: Par): F[ParProto] =
    for {
      sends       <- traverse(x.sends, toProtoSSend[F])
      receives    <- traverse(x.receives, toProtoSReceive[F])
      news        <- traverse(x.news, toProtoSNew[F])
      exprs       <- traverse(x.exprs, toProtoSExpr[F])
      matches     <- traverse(x.matches, toProtoSMatch[F])
      bundles     <- traverse(x.bundles, toProtoSBundle[F])
      connectives <- traverse(x.connectives, toProtoSConnective[F])
    } yield ParProto(
      sends,
      receives,
      news,
      exprs,
      matches,
      x.unforgeables.map(toProto),
      bundles,
      connectives,
      x.locallyFree,
      x.connectiveUsed
    )
  def fromProto(x: ParProto): Par = fromProtoSPar[M](x).value
  def fromProtoSPar[F[_]: Sync](x: ParProto): F[Par] =
    for {
      sends       <- traverse(x.sends, fromProtoSSend[F])
      receives    <- traverse(x.receives, fromProtoSReceive[F])
      news        <- traverse(x.news, fromProtoSNew[F])
      exprs       <- traverse(x.exprs, fromProtoSExpr[F])
      matches     <- traverse(x.matches, fromProtoSMatch[F])
      bundles     <- traverse(x.bundles, fromProtoSBundle[F])
      connectives <- traverse(x.connectives, fromProtoSConnective[F])
    } yield Par(
      sends,
      receives,
      news,
      exprs,
      matches,
      x.unforgeables.map(fromProto),
      bundles,
      connectives,
      x.locallyFree,
      x.connectiveUsed
    )

  def toProto(x: Send): SendProto = toProtoSSend[M](x).value
  private def toProtoSSend[F[_]: Sync](x: Send): F[SendProto] =
    for {
      chan <- evaluate(x.chan, toProtoSPar[F])
      data <- traverse(x.data, toProtoSPar[F])
    } yield SendProto(chan, data, x.persistent, x.locallyFree, x.connectiveUsed)
  def fromProto(x: SendProto): Send = fromProtoSSend[M](x).value
  def fromProtoSSend[F[_]: Sync](x: SendProto): F[Send] =
    for {
      chan <- evaluate(x.chan, fromProtoSPar[F])
      data <- traverse(x.data, fromProtoSPar[F])
    } yield Send(chan, data, x.persistent, x.locallyFree, x.connectiveUsed)

  def toProto(x: Receive): ReceiveProto = toProtoSReceive[M](x).value
  private def toProtoSReceive[F[_]: Sync](x: Receive): F[ReceiveProto] =
    for {
      binds <- traverse(x.binds, toProtoSReceiveBind[F])
      body  <- evaluate(x.body, toProtoSPar[F])
    } yield ReceiveProto(
      binds,
      body,
      x.persistent,
      x.peek,
      x.bindCount,
      x.locallyFree,
      x.connectiveUsed
    )
  def fromProto(x: ReceiveProto): Receive = fromProtoSReceive[M](x).value
  def fromProtoSReceive[F[_]: Sync](x: ReceiveProto): F[Receive] =
    for {
      binds <- traverse(x.binds, fromProtoSReceiveBind[F])
      body  <- evaluate(x.body, fromProtoSPar[F])
    } yield Receive(
      binds,
      body,
      x.persistent,
      x.peek,
      x.bindCount,
      x.locallyFree,
      x.connectiveUsed
    )

  def toProto(x: New): NewProto = toProtoSNew[M](x).value
  private def toProtoSNew[F[_]: Sync](x: New): F[NewProto] =
    for {
      p          <- evaluate(x.p, toProtoSPar[F])
      injections <- traverseMapKeyString(x.injections, toProtoSPar[F])
    } yield NewProto(x.bindCount, p, x.uri, injections, x.locallyFree)
  def fromProto(x: NewProto): New = fromProtoSNew[M](x).value
  def fromProtoSNew[F[_]: Sync](x: NewProto): F[New] =
    for {
      p          <- evaluate(x.p, fromProtoSPar[F])
      injections <- traverseMapKeyString(x.injections, fromProtoSPar[F])
    } yield New(x.bindCount, p, x.uri, injections, x.locallyFree)

  def toProto(x: Expr): ExprProto = toProtoSExpr[M](x).value
  private def toProtoSExpr[F[_]: Sync](expr: Expr): F[ExprProto] =
    expr.exprInstance match {
      case Expr.ExprInstance.Empty => ExprProto(ExprProto.ExprInstance.Empty).pure[F]
      case GBool(x)                => ExprProto(ExprInstance.GBool(x)).pure[F]
      case GInt(x)                 => ExprProto(ExprInstance.GInt(x)).pure[F]
      case GBigInt(x)              => ExprProto(ExprInstance.GBigInt(x)).pure[F]
      case GString(x)              => ExprProto(ExprInstance.GString(x)).pure[F]
      case GUri(x)                 => ExprProto(ExprInstance.GUri(x)).pure[F]
      case GByteArray(x)           => ExprProto(ExprInstance.GByteArray(x)).pure[F]
      case ENotBody(x) =>
        evaluate(x, toProtoSENot[F]).map { y =>
          ExprProto(ExprInstance.ENotBody(y))
        }
      case ENegBody(x) =>
        evaluate(x, toProtoSENeg[F]).map { y =>
          ExprProto(ExprInstance.ENegBody(y))
        }
      case EMultBody(x) =>
        evaluate(x, toProtoSEMult[F]).map { y =>
          ExprProto(ExprInstance.EMultBody(y))
        }
      case EDivBody(x) =>
        evaluate(x, toProtoSEDiv[F]).map { y =>
          ExprProto(ExprInstance.EDivBody(y))
        }
      case EPlusBody(x) =>
        evaluate(x, toProtoSEPlus[F]).map { y =>
          ExprProto(ExprInstance.EPlusBody(y))
        }
      case EMinusBody(x) =>
        evaluate(x, toProtoSEMinus[F]).map { y =>
          ExprProto(ExprInstance.EMinusBody(y))
        }
      case ELtBody(x) =>
        evaluate(x, toProtoSELt[F]).map { y =>
          ExprProto(ExprInstance.ELtBody(y))
        }
      case ELteBody(x) =>
        evaluate(x, toProtoSELte[F]).map { y =>
          ExprProto(ExprInstance.ELteBody(y))
        }
      case EGtBody(x) =>
        evaluate(x, toProtoSEGt[F]).map { y =>
          ExprProto(ExprInstance.EGtBody(y))
        }
      case EGteBody(x) =>
        evaluate(x, toProtoSEGte[F]).map { y =>
          ExprProto(ExprInstance.EGteBody(y))
        }
      case EEqBody(x) =>
        evaluate(x, toProtoSEEq[F]).map { y =>
          ExprProto(ExprInstance.EEqBody(y))
        }
      case ENeqBody(x) =>
        evaluate(x, toProtoSENeq[F]).map { y =>
          ExprProto(ExprInstance.ENeqBody(y))
        }
      case EAndBody(x) =>
        evaluate(x, toProtoSEAnd[F]).map { y =>
          ExprProto(ExprInstance.EAndBody(y))
        }
      case EOrBody(x) =>
        evaluate(x, toProtoSEOr[F]).map { y =>
          ExprProto(ExprInstance.EOrBody(y))
        }
      case EVarBody(x) => ExprProto(ExprInstance.EVarBody(toProto(x))).pure[F]
      case EListBody(x) =>
        evaluate(x, toProtoSEList[F]).map { y =>
          ExprProto(ExprInstance.EListBody(y))
        }
      case ETupleBody(x) =>
        evaluate(x, toProtoSETuple[F]).map { y =>
          ExprProto(ExprInstance.ETupleBody(y))
        }
      case ESetBody(x) =>
        evaluate(x, toProtoSParSet[F]).map { y =>
          ExprProto(ExprInstance.ESetBody(y))
        }
      case EMapBody(x) =>
        evaluate(x, toProtoSParMap[F]).map { y =>
          ExprProto(ExprInstance.EMapBody(y))
        }
      case EMethodBody(x) =>
        evaluate(x, toProtoSEMethod[F]).map { y =>
          ExprProto(ExprInstance.EMethodBody(y))
        }
      case EMatchesBody(x) =>
        evaluate(x, toProtoSEMatches[F]).map { y =>
          ExprProto(ExprInstance.EMatchesBody(y))
        }
      case EPercentPercentBody(x) =>
        evaluate(x, toProtoSEPercentPercent[F]).map { y =>
          ExprProto(ExprInstance.EPercentPercentBody(y))
        }
      case EPlusPlusBody(x) =>
        evaluate(x, toProtoSEPlusPlus[F]).map { y =>
          ExprProto(ExprInstance.EPlusPlusBody(y))
        }
      case EMinusMinusBody(x) =>
        evaluate(x, toProtoSEMinusMinus[F]).map { y =>
          ExprProto(ExprInstance.EMinusMinusBody(y))
        }
      case EModBody(x) =>
        evaluate(x, toProtoSEMod[F]).map { y =>
          ExprProto(ExprInstance.EModBody(y))
        }
      case EShortAndBody(x) =>
        evaluate(x, toProtoSEShortAnd[F]).map { y =>
          ExprProto(ExprInstance.EShortAndBody(y))
        }
      case EShortOrBody(x) =>
        evaluate(x, toProtoSEShortOr[F]).map { y =>
          ExprProto(ExprInstance.EShortOrBody(y))
        }
    }
  def fromProto(x: ExprProto): Expr = fromProtoSExpr[M](x).value
  def fromProtoSExpr[F[_]: Sync](expr: ExprProto): F[Expr] = expr.exprInstance match {
    case ExprProto.ExprInstance.Empty => Expr(Expr.ExprInstance.Empty).pure[F]
    case ExprInstance.GBool(x)        => Expr(GBool(x)).pure[F]
    case ExprInstance.GInt(x)         => Expr(GInt(x)).pure[F]
    case ExprInstance.GBigInt(x)      => Expr(GBigInt(x)).pure[F]
    case ExprInstance.GString(x)      => Expr(GString(x)).pure[F]
    case ExprInstance.GUri(x)         => Expr(GUri(x)).pure[F]
    case ExprInstance.GByteArray(x)   => Expr(GByteArray(x)).pure[F]
    case ExprInstance.ENotBody(x) =>
      evaluate(x, fromProtoSENot[F]).map { y =>
        Expr(ENotBody(y))
      }
    case ExprInstance.ENegBody(x) =>
      evaluate(x, fromProtoSENeg[F]).map { y =>
        Expr(ENegBody(y))
      }
    case ExprInstance.EMultBody(x) =>
      evaluate(x, fromProtoSEMult[F]).map { y =>
        Expr(EMultBody(y))
      }
    case ExprInstance.EDivBody(x) =>
      evaluate(x, fromProtoSEDiv[F]).map { y =>
        Expr(EDivBody(y))
      }
    case ExprInstance.EPlusBody(x) =>
      evaluate(x, fromProtoSEPlus[F]).map { y =>
        Expr(EPlusBody(y))
      }
    case ExprInstance.EMinusBody(x) =>
      evaluate(x, fromProtoSEMinus[F]).map { y =>
        Expr(EMinusBody(y))
      }
    case ExprInstance.ELtBody(x) =>
      evaluate(x, fromProtoSELt[F]).map { y =>
        Expr(ELtBody(y))
      }
    case ExprInstance.ELteBody(x) =>
      evaluate(x, fromProtoSELte[F]).map { y =>
        Expr(ELteBody(y))
      }
    case ExprInstance.EGtBody(x) =>
      evaluate(x, fromProtoSEGt[F]).map { y =>
        Expr(EGtBody(y))
      }
    case ExprInstance.EGteBody(x) =>
      evaluate(x, fromProtoSEGte[F]).map { y =>
        Expr(EGteBody(y))
      }
    case ExprInstance.EEqBody(x) =>
      evaluate(x, fromProtoSEEq[F]).map { y =>
        Expr(EEqBody(y))
      }
    case ExprInstance.ENeqBody(x) =>
      evaluate(x, fromProtoSENeq[F]).map { y =>
        Expr(ENeqBody(y))
      }
    case ExprInstance.EAndBody(x) =>
      evaluate(x, fromProtoSEAnd[F]).map { y =>
        Expr(EAndBody(y))
      }
    case ExprInstance.EOrBody(x) =>
      evaluate(x, fromProtoSEOr[F]).map { y =>
        Expr(EOrBody(y))
      }
    case ExprInstance.EVarBody(x) => Expr(EVarBody(fromProto(x))).pure[F]
    case ExprInstance.EListBody(x) =>
      evaluate(x, fromProtoSEList[F]).map { y =>
        Expr(EListBody(y))
      }
    case ExprInstance.ETupleBody(x) =>
      evaluate(x, fromProtoSETuple[F]).map { y =>
        Expr(ETupleBody(y))
      }
    case ExprInstance.ESetBody(x) =>
      evaluate(x, fromProtoSParSet[F]).map { y =>
        Expr(ESetBody(y))
      }
    case ExprInstance.EMapBody(x) =>
      evaluate(x, fromProtoSParMap[F]).map { y =>
        Expr(EMapBody(y))
      }
    case ExprInstance.EMethodBody(x) =>
      evaluate(x, fromProtoSEMethod[F]).map { y =>
        Expr(EMethodBody(y))
      }
    case ExprInstance.EMatchesBody(x) =>
      evaluate(x, fromProtoSEMatches[F]).map { y =>
        Expr(EMatchesBody(y))
      }
    case ExprInstance.EPercentPercentBody(x) =>
      evaluate(x, fromProtoSEPercentPercent[F]).map { y =>
        Expr(EPercentPercentBody(y))
      }
    case ExprInstance.EPlusPlusBody(x) =>
      evaluate(x, fromProtoSEPlusPlus[F]).map { y =>
        Expr(EPlusPlusBody(y))
      }
    case ExprInstance.EMinusMinusBody(x) =>
      evaluate(x, fromProtoSEMinusMinus[F]).map { y =>
        Expr(EMinusMinusBody(y))
      }
    case ExprInstance.EModBody(x) =>
      evaluate(x, fromProtoSEMod[F]).map { y =>
        Expr(EModBody(y))
      }
    case ExprInstance.EShortAndBody(x) =>
      evaluate(x, fromProtoSEShortAnd[F]).map { y =>
        Expr(EShortAndBody(y))
      }
    case ExprInstance.EShortOrBody(x) =>
      evaluate(x, fromProtoSEShortOr[F]).map { y =>
        Expr(EShortOrBody(y))
      }
  }

  def toProto(x: Match): MatchProto = toProtoSMatch[M](x).value
  private def toProtoSMatch[F[_]: Sync](x: Match): F[MatchProto] =
    for {
      target <- evaluate(x.target, toProtoSPar[F])
      cases  <- traverse(x.cases, toProtoSMatchCase[F])
    } yield MatchProto(target, cases, x.locallyFree, x.connectiveUsed)
  def fromProto(x: MatchProto): Match = fromProtoSMatch[M](x).value
  def fromProtoSMatch[F[_]: Sync](x: MatchProto): F[Match] =
    for {
      target <- evaluate(x.target, fromProtoSPar[F])
      cases  <- traverse(x.cases, fromProtoSMatchCase[F])
    } yield Match(target, cases, x.locallyFree, x.connectiveUsed)

  def toProto(x: MatchCase): MatchCaseProto = toProtoSMatchCase[M](x).value
  private def toProtoSMatchCase[F[_]: Sync](x: MatchCase): F[MatchCaseProto] =
    for {
      pattern <- evaluate(x.pattern, toProtoSPar[F])
      source  <- evaluate(x.source, toProtoSPar[F])
    } yield MatchCaseProto(pattern, source, x.freeCount)
  def fromProto(x: MatchCaseProto): MatchCase = fromProtoSMatchCase[M](x).value
  def fromProtoSMatchCase[F[_]: Sync](x: MatchCaseProto): F[MatchCase] =
    for {
      pattern <- evaluate(x.pattern, fromProtoSPar[F])
      source  <- evaluate(x.source, fromProtoSPar[F])
    } yield MatchCase(pattern, source, x.freeCount)

  def toProto(x: Bundle): BundleProto = toProtoSBundle[M](x).value
  private def toProtoSBundle[F[_]: Sync](x: Bundle): F[BundleProto] =
    for {
      body <- evaluate(x.body, toProtoSPar[F])
    } yield BundleProto(body, x.writeFlag, x.readFlag)
  def fromProto(x: BundleProto): Bundle = fromProtoSBundle[M](x).value
  def fromProtoSBundle[F[_]: Sync](x: BundleProto): F[Bundle] =
    for {
      body <- evaluate(x.body, fromProtoSPar[F])
    } yield Bundle(body, x.writeFlag, x.readFlag)

  def toProto(x: GUnforgeable): GUnforgeableProto = GUnforgeableProto(
    x.unfInstance match {
      case UnfInstance.Empty            => GUnforgeableProto.UnfInstance.Empty
      case UnfInstance.GPrivateBody(x)  => GUnforgeableProto.UnfInstance.GPrivateBody(toProto(x))
      case UnfInstance.GDeployIdBody(x) => GUnforgeableProto.UnfInstance.GDeployIdBody(toProto(x))
      case UnfInstance.GDeployerIdBody(x) =>
        GUnforgeableProto.UnfInstance.GDeployerIdBody(toProto(x))
      case UnfInstance.GSysAuthTokenBody(x) =>
        GUnforgeableProto.UnfInstance.GSysAuthTokenBody(toProto(x))
    }
  )
  def fromProto(x: GUnforgeableProto): GUnforgeable = GUnforgeable(
    x.unfInstance match {
      case GUnforgeableProto.UnfInstance.Empty            => UnfInstance.Empty
      case GUnforgeableProto.UnfInstance.GPrivateBody(x)  => UnfInstance.GPrivateBody(fromProto(x))
      case GUnforgeableProto.UnfInstance.GDeployIdBody(x) => UnfInstance.GDeployIdBody(fromProto(x))
      case GUnforgeableProto.UnfInstance.GDeployerIdBody(x) =>
        UnfInstance.GDeployerIdBody(fromProto(x))
      case GUnforgeableProto.UnfInstance.GSysAuthTokenBody(x) =>
        UnfInstance.GSysAuthTokenBody(fromProto(x))
    }
  )

  def toProto(x: GPrivate): GPrivateProto   = GPrivateProto(x.id)
  def fromProto(x: GPrivateProto): GPrivate = GPrivate(x.id)

  def toProto(x: GDeployId): GDeployIdProto   = GDeployIdProto(x.sig)
  def fromProto(x: GDeployIdProto): GDeployId = GDeployId(x.sig)

  def toProto(x: GDeployerId): GDeployerIdProto   = GDeployerIdProto(x.publicKey)
  def fromProto(x: GDeployerIdProto): GDeployerId = GDeployerId(x.publicKey)

  def toProto(x: GSysAuthToken): GSysAuthTokenProto   = GSysAuthTokenProto()
  def fromProto(x: GSysAuthTokenProto): GSysAuthToken = GSysAuthToken()

  def toProto(x: Connective): ConnectiveProto = toProtoSConnective[M](x).value
  private def toProtoSConnective[F[_]: Sync](conn: Connective): F[ConnectiveProto] =
    conn.connectiveInstance match {
      case Connective.ConnectiveInstance.Empty =>
        ConnectiveProto(ConnectiveProto.ConnectiveInstance.Empty).pure[F]
      case ConnAndBody(x) =>
        evaluate(x, toProtoSConnectiveBody[F]).map { y =>
          ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnOrBody(y))
        }
      case ConnOrBody(x) =>
        evaluate(x, toProtoSConnectiveBody[F]).map { y =>
          ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnOrBody(y))
        }
      case ConnNotBody(x) =>
        evaluate(x, toProtoSPar[F]).map { y =>
          ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnNotBody(y))
        }
      case VarRefBody(x) =>
        ConnectiveProto(ConnectiveProto.ConnectiveInstance.VarRefBody(toProto(x))).pure[F]
      case ConnBool(x) => ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnBool(x)).pure[F]
      case ConnInt(x)  => ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnInt(x)).pure[F]
      case ConnBigInt(x) =>
        ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnBigInt(x)).pure[F]
      case ConnString(x) =>
        ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnString(x)).pure[F]
      case ConnUri(x) => ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnUri(x)).pure[F]
      case ConnByteArray(x) =>
        ConnectiveProto(ConnectiveProto.ConnectiveInstance.ConnByteArray(x)).pure[F]
    }
  def fromProto(x: ConnectiveProto): Connective = fromProtoSConnective[M](x).value
  def fromProtoSConnective[F[_]: Sync](conn: ConnectiveProto): F[Connective] =
    conn.connectiveInstance match {
      case ConnectiveProto.ConnectiveInstance.Empty =>
        Connective(Connective.ConnectiveInstance.Empty).pure[F]
      case ConnectiveProto.ConnectiveInstance.ConnAndBody(x) =>
        evaluate(x, fromProtoSConnectiveBody[F]).map { y =>
          Connective(ConnOrBody(y))
        }
      case ConnectiveProto.ConnectiveInstance.ConnOrBody(x) =>
        evaluate(x, fromProtoSConnectiveBody[F]).map { y =>
          Connective(ConnOrBody(y))
        }
      case ConnectiveProto.ConnectiveInstance.ConnNotBody(x) =>
        evaluate(x, fromProtoSPar[F]).map { y =>
          Connective(ConnNotBody(y))
        }
      case ConnectiveProto.ConnectiveInstance.VarRefBody(x) =>
        Connective(VarRefBody(fromProto(x))).pure[F]
      case ConnectiveProto.ConnectiveInstance.ConnBool(x) => Connective(ConnBool(x)).pure[F]
      case ConnectiveProto.ConnectiveInstance.ConnInt(x)  => Connective(ConnInt(x)).pure[F]
      case ConnectiveProto.ConnectiveInstance.ConnBigInt(x) =>
        Connective(ConnBigInt(x)).pure[F]
      case ConnectiveProto.ConnectiveInstance.ConnString(x) =>
        Connective(ConnString(x)).pure[F]
      case ConnectiveProto.ConnectiveInstance.ConnUri(x) => Connective(ConnUri(x)).pure[F]
      case ConnectiveProto.ConnectiveInstance.ConnByteArray(x) =>
        Connective(ConnByteArray(x)).pure[F]
    }

  def toProto(x: ConnectiveBody): ConnectiveBodyProto = toProtoSConnectiveBody[M](x).value
  private def toProtoSConnectiveBody[F[_]: Sync](x: ConnectiveBody): F[ConnectiveBodyProto] =
    for {
      ps <- traverse(x.ps, toProtoSPar[F])
    } yield ConnectiveBodyProto(ps)
  def fromProto(x: ConnectiveBodyProto): ConnectiveBody = fromProtoSConnectiveBody[M](x).value
  def fromProtoSConnectiveBody[F[_]: Sync](x: ConnectiveBodyProto): F[ConnectiveBody] =
    for {
      ps <- traverse(x.ps, fromProtoSPar[F])
    } yield ConnectiveBody(ps)

  def toProto(x: VarRef): VarRefProto   = VarRefProto(x.index, x.depth)
  def fromProto(x: VarRefProto): VarRef = VarRef(x.index, x.depth)

  def toProto(x: ReceiveBind): ReceiveBindProto = toProtoSReceiveBind[M](x).value
  private def toProtoSReceiveBind[F[_]: Sync](x: ReceiveBind): F[ReceiveBindProto] =
    for {
      patterns <- traverse(x.patterns, toProtoSPar[F])
      source   <- evaluate(x.source, toProtoSPar[F])
    } yield ReceiveBindProto(
      patterns,
      source,
      x.remainder.map(toProto),
      x.freeCount
    )
  def fromProto(x: ReceiveBindProto): ReceiveBind = fromProtoSReceiveBind[M](x).value
  def fromProtoSReceiveBind[F[_]: Sync](x: ReceiveBindProto): F[ReceiveBind] =
    for {
      patterns <- traverse(x.patterns, fromProtoSPar[F])
      source   <- evaluate(x.source, fromProtoSPar[F])
    } yield ReceiveBind(
      patterns,
      source,
      x.remainder.map(fromProto),
      x.freeCount
    )

  def toProto(v: Var): VarProto = VarProto(
    v.varInstance match {
      case VarInstance.Empty       => VarProto.VarInstance.Empty
      case VarInstance.BoundVar(x) => VarProto.VarInstance.BoundVar(x)
      case VarInstance.FreeVar(x)  => VarProto.VarInstance.FreeVar(x)
      case VarInstance.Wildcard(_) =>
        import io.rhonix.models.protobuf.VarProto.WildcardMsgProto
        VarProto.VarInstance.Wildcard(WildcardMsgProto())
    }
  )
  def fromProto(v: VarProto): Var = Var(
    v.varInstance match {
      case VarProto.VarInstance.Empty       => VarInstance.Empty
      case VarProto.VarInstance.BoundVar(x) => VarInstance.BoundVar(x)
      case VarProto.VarInstance.FreeVar(x)  => VarInstance.FreeVar(x)
      case VarProto.VarInstance.Wildcard(_) =>
        import io.rhonix.models.Var.WildcardMsg
        VarInstance.Wildcard(WildcardMsg())
    }
  )

  def toProto(x: ENot): ENotProto = toProtoSENot[M](x).value
  private def toProtoSENot[F[_]: Sync](x: ENot): F[ENotProto] =
    for {
      p <- evaluate(x.p, toProtoSPar[F])
    } yield ENotProto(p)
  def fromProto(x: ENotProto): ENot = fromProtoSENot[M](x).value
  def fromProtoSENot[F[_]: Sync](x: ENotProto): F[ENot] =
    for {
      p <- evaluate(x.p, fromProtoSPar[F])
    } yield ENot(p)

  def toProto(x: ENeg): ENegProto = toProtoSENeg[M](x).value
  private def toProtoSENeg[F[_]: Sync](x: ENeg): F[ENegProto] =
    for {
      p <- evaluate(x.p, toProtoSPar[F])
    } yield ENegProto(p)
  def fromProto(x: ENegProto): ENeg = fromProtoSENeg[M](x).value
  def fromProtoSENeg[F[_]: Sync](x: ENegProto): F[ENeg] =
    for {
      p <- evaluate(x.p, fromProtoSPar[F])
    } yield ENeg(p)

  def toProto(x: EMult): EMultProto = toProtoSEMult[M](x).value
  private def toProtoSEMult[F[_]: Sync](x: EMult): F[EMultProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EMultProto(p1, p2)
  def fromProto(x: EMultProto): EMult = fromProtoSEMult[M](x).value
  def fromProtoSEMult[F[_]: Sync](x: EMultProto): F[EMult] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EMult(p1, p2)

  def toProto(x: EDiv): EDivProto = toProtoSEDiv[M](x).value
  private def toProtoSEDiv[F[_]: Sync](x: EDiv): F[EDivProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EDivProto(p1, p2)
  def fromProto(x: EDivProto): EDiv = fromProtoSEDiv[M](x).value
  def fromProtoSEDiv[F[_]: Sync](x: EDivProto): F[EDiv] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EDiv(p1, p2)

  def toProto(x: EPlus): EPlusProto = toProtoSEPlus[M](x).value
  private def toProtoSEPlus[F[_]: Sync](x: EPlus): F[EPlusProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EPlusProto(p1, p2)
  def fromProto(x: EPlusProto): EPlus = fromProtoSEPlus[M](x).value
  def fromProtoSEPlus[F[_]: Sync](x: EPlusProto): F[EPlus] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EPlus(p1, p2)

  def toProto(x: EMinus): EMinusProto = toProtoSEMinus[M](x).value
  private def toProtoSEMinus[F[_]: Sync](x: EMinus): F[EMinusProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EMinusProto(p1, p2)
  def fromProto(x: EMinusProto): EMinus = fromProtoSEMinus[M](x).value
  def fromProtoSEMinus[F[_]: Sync](x: EMinusProto): F[EMinus] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EMinus(p1, p2)

  def toProto(x: ELt): ELtProto = toProtoSELt[M](x).value
  private def toProtoSELt[F[_]: Sync](x: ELt): F[ELtProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield ELtProto(p1, p2)
  def fromProto(x: ELtProto): ELt = fromProtoSELt[M](x).value
  def fromProtoSELt[F[_]: Sync](x: ELtProto): F[ELt] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield ELt(p1, p2)

  def toProto(x: ELte): ELteProto = toProtoSELte[M](x).value
  private def toProtoSELte[F[_]: Sync](x: ELte): F[ELteProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield ELteProto(p1, p2)
  def fromProto(x: ELteProto): ELte = fromProtoSELte[M](x).value
  def fromProtoSELte[F[_]: Sync](x: ELteProto): F[ELte] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield ELte(p1, p2)

  def toProto(x: EGt): EGtProto = toProtoSEGt[M](x).value
  private def toProtoSEGt[F[_]: Sync](x: EGt): F[EGtProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EGtProto(p1, p2)
  def fromProto(x: EGtProto): EGt = fromProtoSEGt[M](x).value
  def fromProtoSEGt[F[_]: Sync](x: EGtProto): F[EGt] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EGt(p1, p2)

  def toProto(x: EGte): EGteProto = toProtoSEGte[M](x).value
  private def toProtoSEGte[F[_]: Sync](x: EGte): F[EGteProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EGteProto(p1, p2)
  def fromProto(x: EGteProto): EGte = fromProtoSEGte[M](x).value
  def fromProtoSEGte[F[_]: Sync](x: EGteProto): F[EGte] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EGte(p1, p2)

  def toProto(x: EEq): EEqProto = toProtoSEEq[M](x).value
  private def toProtoSEEq[F[_]: Sync](x: EEq): F[EEqProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EEqProto(p1, p2)
  def fromProto(x: EEqProto): EEq = fromProtoSEEq[M](x).value
  def fromProtoSEEq[F[_]: Sync](x: EEqProto): F[EEq] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EEq(p1, p2)

  def toProto(x: ENeq): ENeqProto = toProtoSENeq[M](x).value
  private def toProtoSENeq[F[_]: Sync](x: ENeq): F[ENeqProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield ENeqProto(p1, p2)
  def fromProto(x: ENeqProto): ENeq = fromProtoSENeq[M](x).value
  def fromProtoSENeq[F[_]: Sync](x: ENeqProto): F[ENeq] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield ENeq(p1, p2)

  def toProto(x: EAnd): EAndProto = toProtoSEAnd[M](x).value
  private def toProtoSEAnd[F[_]: Sync](x: EAnd): F[EAndProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EAndProto(p1, p2)
  def fromProto(x: EAndProto): EAnd = fromProtoSEAnd[M](x).value
  def fromProtoSEAnd[F[_]: Sync](x: EAndProto): F[EAnd] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EAnd(p1, p2)

  def toProto(x: EOr): EOrProto = toProtoSEOr[M](x).value
  private def toProtoSEOr[F[_]: Sync](x: EOr): F[EOrProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EOrProto(p1, p2)
  def fromProto(x: EOrProto): EOr = fromProtoSEOr[M](x).value
  def fromProtoSEOr[F[_]: Sync](x: EOrProto): F[EOr] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EOr(p1, p2)

  def toProto(x: EVar): EVarProto   = EVarProto(toProto(x.v))
  def fromProto(x: EVarProto): EVar = EVar(fromProto(x.v))

  def toProto(x: EList): EListProto = toProtoSEList[M](x).value
  private def toProtoSEList[F[_]: Sync](x: EList): F[EListProto] =
    for {
      ps <- traverse(x.ps, toProtoSPar[F])
    } yield EListProto(ps, x.locallyFree, x.connectiveUsed, x.remainder.map(toProto))
  def fromProto(x: EListProto): EList = fromProtoSEList[M](x).value
  def fromProtoSEList[F[_]: Sync](x: EListProto): F[EList] =
    for {
      ps <- traverse(x.ps, fromProtoSPar[F])
    } yield EList(ps, x.locallyFree, x.connectiveUsed, x.remainder.map(fromProto))

  def toProto(x: ETuple): ETupleProto = toProtoSETuple[M](x).value
  private def toProtoSETuple[F[_]: Sync](x: ETuple): F[ETupleProto] =
    for {
      ps <- traverse(x.ps, toProtoSPar[F])
    } yield ETupleProto(ps, x.locallyFree, x.connectiveUsed)
  def fromProto(x: ETupleProto): ETuple = fromProtoSETuple[M](x).value
  def fromProtoSETuple[F[_]: Sync](x: ETupleProto): F[ETuple] =
    for {
      ps <- traverse(x.ps, fromProtoSPar[F])
    } yield ETuple(ps, x.locallyFree, x.connectiveUsed)

  def toProto(x: EMethod): EMethodProto = toProtoSEMethod[M](x).value
  private def toProtoSEMethod[F[_]: Sync](x: EMethod): F[EMethodProto] =
    for {
      target    <- evaluate(x.target, toProtoSPar[F])
      arguments <- traverse(x.arguments, toProtoSPar[F])
    } yield EMethodProto(x.methodName, target, arguments, x.locallyFree, x.connectiveUsed)
  def fromProto(x: EMethodProto): EMethod = fromProtoSEMethod[M](x).value
  def fromProtoSEMethod[F[_]: Sync](x: EMethodProto): F[EMethod] =
    for {
      target    <- evaluate(x.target, fromProtoSPar[F])
      arguments <- traverse(x.arguments, fromProtoSPar[F])
    } yield EMethod(x.methodName, target, arguments, x.locallyFree, x.connectiveUsed)

  def toProto(x: EMatches): EMatchesProto = toProtoSEMatches[M](x).value
  private def toProtoSEMatches[F[_]: Sync](x: EMatches): F[EMatchesProto] =
    for {
      target  <- evaluate(x.target, toProtoSPar[F])
      pattern <- evaluate(x.pattern, toProtoSPar[F])
    } yield EMatchesProto(target, pattern)
  def fromProto(x: EMatchesProto): EMatches = fromProtoSEMatches[M](x).value
  def fromProtoSEMatches[F[_]: Sync](x: EMatchesProto): F[EMatches] =
    for {
      target  <- evaluate(x.target, fromProtoSPar[F])
      pattern <- evaluate(x.pattern, fromProtoSPar[F])
    } yield EMatches(target, pattern)

  def toProto(x: EPercentPercent): EPercentPercentProto = toProtoSEPercentPercent[M](x).value
  private def toProtoSEPercentPercent[F[_]: Sync](x: EPercentPercent): F[EPercentPercentProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EPercentPercentProto(p1, p2)
  def fromProto(x: EPercentPercentProto): EPercentPercent = fromProtoSEPercentPercent[M](x).value
  def fromProtoSEPercentPercent[F[_]: Sync](x: EPercentPercentProto): F[EPercentPercent] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EPercentPercent(p1, p2)

  def toProto(x: EPlusPlus): EPlusPlusProto = toProtoSEPlusPlus[M](x).value
  private def toProtoSEPlusPlus[F[_]: Sync](x: EPlusPlus): F[EPlusPlusProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EPlusPlusProto(p1, p2)
  def fromProto(x: EPlusPlusProto): EPlusPlus = fromProtoSEPlusPlus[M](x).value
  def fromProtoSEPlusPlus[F[_]: Sync](x: EPlusPlusProto): F[EPlusPlus] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EPlusPlus(p1, p2)

  def toProto(x: EMinusMinus): EMinusMinusProto = toProtoSEMinusMinus[M](x).value
  private def toProtoSEMinusMinus[F[_]: Sync](x: EMinusMinus): F[EMinusMinusProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EMinusMinusProto(p1, p2)
  def fromProto(x: EMinusMinusProto): EMinusMinus = fromProtoSEMinusMinus[M](x).value
  def fromProtoSEMinusMinus[F[_]: Sync](x: EMinusMinusProto): F[EMinusMinus] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EMinusMinus(p1, p2)

  def toProto(x: EMod): EModProto = toProtoSEMod[M](x).value
  private def toProtoSEMod[F[_]: Sync](x: EMod): F[EModProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EModProto(p1, p2)
  def fromProto(x: EModProto): EMod = fromProtoSEMod[M](x).value
  def fromProtoSEMod[F[_]: Sync](x: EModProto): F[EMod] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EMod(p1, p2)

  def toProto(x: EShortAnd): EShortAndProto = toProtoSEShortAnd[M](x).value
  private def toProtoSEShortAnd[F[_]: Sync](x: EShortAnd): F[EShortAndProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EShortAndProto(p1, p2)
  def fromProto(x: EShortAndProto): EShortAnd = fromProtoSEShortAnd[M](x).value
  def fromProtoSEShortAnd[F[_]: Sync](x: EShortAndProto): F[EShortAnd] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EShortAnd(p1, p2)

  def toProto(x: EShortOr): EShortOrProto = toProtoSEShortOr[M](x).value
  private def toProtoSEShortOr[F[_]: Sync](x: EShortOr): F[EShortOrProto] =
    for {
      p1 <- evaluate(x.p1, toProtoSPar[F])
      p2 <- evaluate(x.p2, toProtoSPar[F])
    } yield EShortOrProto(p1, p2)
  def fromProto(x: EShortOrProto): EShortOr = fromProtoSEShortOr[M](x).value
  def fromProtoSEShortOr[F[_]: Sync](x: EShortOrProto): F[EShortOr] =
    for {
      p1 <- evaluate(x.p1, fromProtoSPar[F])
      p2 <- evaluate(x.p2, fromProtoSPar[F])
    } yield EShortOr(p1, p2)

  def toProto(x: TaggedContinuation): TaggedContinuationProto =
    toProtoSTaggedContinuation[M](x).value
  private def toProtoSTaggedContinuation[F[_]: Sync](
      tc: TaggedContinuation
  ): F[TaggedContinuationProto] =
    tc.taggedCont match {
      case TaggedCont.Empty =>
        TaggedContinuationProto(TaggedContinuationProto.TaggedCont.Empty).pure[F]
      case TaggedCont.ParBody(x) =>
        evaluate(x, toProtoSParWithRandom[F]).map { y =>
          TaggedContinuationProto(TaggedContinuationProto.TaggedCont.ParBody(y))
        }
      case TaggedCont.ScalaBodyRef(x) =>
        TaggedContinuationProto(TaggedContinuationProto.TaggedCont.ScalaBodyRef(x)).pure[F]
    }
  def fromProto(x: TaggedContinuationProto): TaggedContinuation =
    fromProtoSTaggedContinuation[M](x).value
  def fromProtoSTaggedContinuation[F[_]: Sync](tc: TaggedContinuationProto): F[TaggedContinuation] =
    tc.taggedCont match {
      case TaggedContinuationProto.TaggedCont.Empty =>
        TaggedContinuation(TaggedContinuation.TaggedCont.Empty).pure[F]
      case TaggedContinuationProto.TaggedCont.ParBody(x) =>
        evaluate(x, fromProtoSParWithRandom[F]).map { y =>
          TaggedContinuation(TaggedContinuation.TaggedCont.ParBody(y))
        }
      case TaggedContinuationProto.TaggedCont.ScalaBodyRef(x) =>
        TaggedContinuation(TaggedContinuation.TaggedCont.ScalaBodyRef(x)).pure[F]
    }

  def toProto(x: ESet): ESetProto = toProtoSESet[M](x).value
  private def toProtoSESet[F[_]: Sync](x: ESet): F[ESetProto] =
    for {
      ps <- traverse(x.ps, toProtoSPar[F])
    } yield ESetProto(ps, x.locallyFree, x.connectiveUsed, x.remainder.map(toProto))
  def fromProto(x: ESetProto): ESet = fromProtoSESet[M](x).value
  def fromProtoSESet[F[_]: Sync](x: ESetProto): F[ESet] =
    for {
      ps <- traverse(x.ps, fromProtoSPar[F])
    } yield ESet(ps, x.locallyFree, x.connectiveUsed, x.remainder.map(fromProto))

  def toProto(x: EMap): EMapProto = toProtoSEMap[M](x).value
  private def toProtoSEMap[F[_]: Sync](x: EMap): F[EMapProto] =
    for {
      kvs <- traverse(x.kvs, toProtoSKeyValuePair[F])
    } yield EMapProto(kvs, x.locallyFree, x.connectiveUsed, x.remainder.map(toProto))
  def fromProto(x: EMapProto): EMap = fromProtoSEMap[M](x).value
  def fromProtoSEMap[F[_]: Sync](x: EMapProto): F[EMap] =
    for {
      kvs <- traverse(x.kvs, fromProtoSKeyValuePair[F])
    } yield EMap(kvs, x.locallyFree, x.connectiveUsed, x.remainder.map(fromProto))

  def toProto(x: ParWithRandom): ParWithRandomProto = toProtoSParWithRandom[M](x).value
  private def toProtoSParWithRandom[F[_]: Sync](x: ParWithRandom): F[ParWithRandomProto] =
    for {
      body <- evaluate(x.body, toProtoSPar[F])
    } yield ParWithRandomProto(body, x.randomState)
  def fromProto(x: ParWithRandomProto): ParWithRandom = fromProtoSParWithRandom[M](x).value
  def fromProtoSParWithRandom[F[_]: Sync](x: ParWithRandomProto): F[ParWithRandom] =
    for {
      body <- evaluate(x.body, fromProtoSPar[F])
    } yield ParWithRandom(body, x.randomState)

  def toProto(x: PCost): PCostProto   = PCostProto(x.cost)
  def fromProto(x: PCostProto): PCost = PCost(x.cost)

  def toProto(x: ListParWithRandom): ListParWithRandomProto = toProtoSListParWithRandom[M](x).value
  private def toProtoSListParWithRandom[F[_]: Sync](
      x: ListParWithRandom
  ): F[ListParWithRandomProto] =
    for {
      pars <- traverse(x.pars, toProtoSPar[F])
    } yield ListParWithRandomProto(pars, x.randomState)
  def fromProto(x: ListParWithRandomProto): ListParWithRandom =
    fromProtoSListParWithRandom[M](x).value
  def fromProtoSListParWithRandom[F[_]: Sync](x: ListParWithRandomProto): F[ListParWithRandom] =
    for {
      pars <- traverse(x.pars, fromProtoSPar[F])
    } yield ListParWithRandom(pars, x.randomState)

  def toProto(x: BindPattern): BindPatternProto = toProtoSBindPattern[M](x).value
  private def toProtoSBindPattern[F[_]: Sync](x: BindPattern): F[BindPatternProto] =
    for {
      patterns <- traverse(x.patterns, toProtoSPar[F])
    } yield BindPatternProto(patterns, x.remainder.map(toProto), x.freeCount)
  def fromProto(x: BindPatternProto): BindPattern = fromProtoSBindPattern[M](x).value
  def fromProtoSBindPattern[F[_]: Sync](x: BindPatternProto): F[BindPattern] =
    for {
      patterns <- traverse(x.patterns, fromProtoSPar[F])
    } yield BindPattern(patterns, x.remainder.map(fromProto), x.freeCount)

  def toProto(x: ListBindPatterns): ListBindPatternsProto = toProtoSListBindPatterns[M](x).value
  private def toProtoSListBindPatterns[F[_]: Sync](x: ListBindPatterns): F[ListBindPatternsProto] =
    for {
      patterns <- traverse(x.patterns, toProtoSBindPattern[F])
    } yield ListBindPatternsProto(patterns)
  def fromProto(x: ListBindPatternsProto): ListBindPatterns = fromProtoSListBindPatterns[M](x).value
  def fromProtoSListBindPatterns[F[_]: Sync](x: ListBindPatternsProto): F[ListBindPatterns] =
    for {
      patterns <- traverse(x.patterns, fromProtoSBindPattern[F])
    } yield ListBindPatterns(patterns)

  def toProto(x: KeyValuePair): KeyValuePairProto = toProtoSKeyValuePair[M](x).value
  private def toProtoSKeyValuePair[F[_]: Sync](x: KeyValuePair): F[KeyValuePairProto] =
    for {
      key   <- evaluate(x.key, toProtoSPar[F])
      value <- evaluate(x.value, toProtoSPar[F])
    } yield KeyValuePairProto(key, value)
  def fromProto(x: KeyValuePairProto): KeyValuePair = fromProtoSKeyValuePair[M](x).value
  def fromProtoSKeyValuePair[F[_]: Sync](x: KeyValuePairProto): F[KeyValuePair] =
    for {
      key   <- evaluate(x.key, fromProtoSPar[F])
      value <- evaluate(x.value, fromProtoSPar[F])
    } yield KeyValuePair(key, value)

  def toProto(x: DeployId): DeployIdProto   = DeployIdProto(x.sig)
  def fromProto(x: DeployIdProto): DeployId = DeployId(x.sig)

  def toProto(x: DeployerId): DeployerIdProto   = DeployerIdProto(x.publicKey)
  def fromProto(x: DeployerIdProto): DeployerId = DeployerId(x.publicKey)

  def toProto(x: ParSet): ParSetProto = toProtoSParSet[M](x).value
  private def toProtoSParSet[F[_]: Sync](x: ParSet): F[ParSetProto] =
    for {
      ps <- evaluate(x.ps, toProtoSSortedParHashSet[F])
    } yield new ParSetProto(ps, x.connectiveUsed, x.locallyFree, x.remainder.map(toProto))
  def fromProto(x: ParSetProto): ParSet = fromProtoSParSet[M](x).value
  def fromProtoSParSet[F[_]: Sync](x: ParSetProto): F[ParSet] =
    for {
      ps <- evaluate(x.ps, fromProtoSSortedParHashSet[F])
    } yield new ParSet(ps, x.connectiveUsed, x.locallyFree, x.remainder.map(fromProto))

  def toProto(x: ParMap): ParMapProto = toProtoSParMap[M](x).value
  private def toProtoSParMap[F[_]: Sync](x: ParMap): F[ParMapProto] =
    for {
      ps <- evaluate(x.ps, toProtoSSortedParMap[F])
    } yield new ParMapProto(ps, x.connectiveUsed, x.locallyFree, x.remainder.map(toProto))
  def fromProto(x: ParMapProto): ParMap = fromProtoSParMap[M](x).value
  def fromProtoSParMap[F[_]: Sync](x: ParMapProto): F[ParMap] =
    for {
      ps <- evaluate(x.ps, fromProtoSSortedParMap[F])
    } yield new ParMap(ps, x.connectiveUsed, x.locallyFree, x.remainder.map(fromProto))

  def toProto(x: SortedParHashSet): SortedParHashSetProto = toProtoSSortedParHashSet[M](x).value
  private def toProtoSSortedParHashSet[F[_]: Sync](x: SortedParHashSet): F[SortedParHashSetProto] =
    for {
      sortedPars <- traverse(x.sortedPars, toProtoSPar[F])
    } yield SortedParHashSetProto(sortedPars)
  def fromProto(x: SortedParHashSetProto): SortedParHashSet = fromProtoSSortedParHashSet[M](x).value
  def fromProtoSSortedParHashSet[F[_]: Sync](x: SortedParHashSetProto): F[SortedParHashSet] =
    for {
      sortedPars <- traverse(x.sortedPars, fromProtoSPar[F])
    } yield SortedParHashSet(sortedPars)

  def toProto(x: SortedParMap): SortedParMapProto = toProtoSSortedParMap[M](x).value
  private def toProtoSSortedParMap[F[_]: Sync](x: SortedParMap): F[SortedParMapProto] =
    for {
      sortedList <- traverseMapList(x.sortedList, toProtoSPar[F])
    } yield SortedParMapProto(sortedList)
  def fromProto(x: SortedParMapProto): SortedParMap = fromProtoSSortedParMap[M](x).value
  def fromProtoSSortedParMap[F[_]: Sync](x: SortedParMapProto): F[SortedParMap] =
    for {
      sortedList <- traverseMapList(x.sortedList, fromProtoSPar[F])
    } yield SortedParMap(sortedList)
}
