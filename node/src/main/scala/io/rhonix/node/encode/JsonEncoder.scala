package io.rhonix.node.encode

import com.google.protobuf.ByteString
import io.rhonix.casper.PrettyPrinter
import io.rhonix.casper.protocol.{BondInfo, JustificationInfo, LightBlockInfo}
import io.rhonix.crypto.hash.Blake2b512Random
import io.rhonix.models.AlwaysEqual
import io.rhonix.models.protobuf.ConnectiveProto.ConnectiveInstance
import io.rhonix.models.protobuf.ExprProto.ExprInstance
import io.rhonix.models.protobuf.GUnforgeableProto.UnfInstance
import io.rhonix.models.protobuf.VarProto._
import io.rhonix.models.protobuf._
import io.rhonix.models.syntax._

import scala.collection.immutable.BitSet

object JsonEncoder {
  import io.circe.Decoder._
  import io.circe.Encoder._
  import io.circe._
  import io.circe.generic.semiauto._

  implicit val encodeByteString: Encoder[ByteString] =
    Encoder.encodeString.contramap[ByteString](PrettyPrinter.buildStringNoLimit)
  implicit val encodeBondInfo: Encoder[BondInfo] = deriveEncoder[BondInfo]
  implicit val encodeJustificationInfo: Encoder[JustificationInfo] =
    deriveEncoder[JustificationInfo]
  implicit val encodeLightBlockInfo: Encoder[LightBlockInfo] = deriveEncoder[LightBlockInfo]
  implicit val encodePar: Encoder[ParProto]                  = deriveEncoder[ParProto]
  implicit val encodeSend: Encoder[SendProto]                = deriveEncoder[SendProto]
  implicit val encodeWildcardMsg: Encoder[WildcardMsgProto]  = deriveEncoder[WildcardMsgProto]
  implicit val encodeVarInstance: Encoder[VarInstance]       = deriveEncoder[VarInstance]
  implicit val encodeVar: Encoder[VarProto]                  = deriveEncoder[VarProto]
  implicit val encodeReceiveBind: Encoder[ReceiveBindProto]  = deriveEncoder[ReceiveBindProto]
  implicit val encodeReceive: Encoder[ReceiveProto]          = deriveEncoder[ReceiveProto]
  implicit val encodeNew: Encoder[NewProto]                  = deriveEncoder[NewProto]
  implicit val encodeENot: Encoder[ENotProto]                = deriveEncoder[ENotProto]
  implicit val encodeENeg: Encoder[ENegProto]                = deriveEncoder[ENegProto]
  implicit val encodeEMult: Encoder[EMultProto]              = deriveEncoder[EMultProto]
  implicit val encodeEDiv: Encoder[EDivProto]                = deriveEncoder[EDivProto]
  implicit val encodeEPlus: Encoder[EPlusProto]              = deriveEncoder[EPlusProto]
  implicit val encodeEMinus: Encoder[EMinusProto]            = deriveEncoder[EMinusProto]
  implicit val encodeELt: Encoder[ELtProto]                  = deriveEncoder[ELtProto]
  implicit val encodeELte: Encoder[ELteProto]                = deriveEncoder[ELteProto]
  implicit val encodeEGt: Encoder[EGtProto]                  = deriveEncoder[EGtProto]
  implicit val encodeEGte: Encoder[EGteProto]                = deriveEncoder[EGteProto]
  implicit val encodeEEq: Encoder[EEqProto]                  = deriveEncoder[EEqProto]
  implicit val encodeENeq: Encoder[ENeqProto]                = deriveEncoder[ENeqProto]
  implicit val encodeEAnd: Encoder[EAndProto]                = deriveEncoder[EAndProto]
  implicit val encodeEOr: Encoder[EOrProto]                  = deriveEncoder[EOrProto]
  implicit val encodeEShortAnd: Encoder[EShortAndProto]      = deriveEncoder[EShortAndProto]
  implicit val encodeEShortOr: Encoder[EShortOrProto]        = deriveEncoder[EShortOrProto]
  implicit val encodeEVar: Encoder[EVarProto]                = deriveEncoder[EVarProto]
  implicit val encodeEList: Encoder[EListProto]              = deriveEncoder[EListProto]
  implicit val encodeETuple: Encoder[ETupleProto]            = deriveEncoder[ETupleProto]
  implicit val encodeParSet: Encoder[ParSetProto] =
    Encoder.encodeList[ParProto].contramapArray[ParSetProto](s => s.ps.iterator.toList)
  implicit val encodeParMap: Encoder[ParMapProto] =
    Encoder.encodeList[(ParProto, ParProto)].contramap[ParMapProto](m => m.ps.iterator.toList)
  implicit val encodeEMethod: Encoder[EMethodProto]   = deriveEncoder[EMethodProto]
  implicit val encodeEMatches: Encoder[EMatchesProto] = deriveEncoder[EMatchesProto]
  implicit val encodeEPercentPercent: Encoder[EPercentPercentProto] =
    deriveEncoder[EPercentPercentProto]
  implicit val encodeEPlusPlus: Encoder[EPlusPlusProto]         = deriveEncoder[EPlusPlusProto]
  implicit val encodeEMinusMinus: Encoder[EMinusMinusProto]     = deriveEncoder[EMinusMinusProto]
  implicit val encodeEMod: Encoder[EModProto]                   = deriveEncoder[EModProto]
  implicit val encodeExprInstance: Encoder[ExprInstance]        = deriveEncoder[ExprInstance]
  implicit val encodeExpr: Encoder[ExprProto]                   = deriveEncoder[ExprProto]
  implicit val encodeMatchCase: Encoder[MatchCaseProto]         = deriveEncoder[MatchCaseProto]
  implicit val encodeMatch: Encoder[MatchProto]                 = deriveEncoder[MatchProto]
  implicit val encodeGPrivate: Encoder[GPrivateProto]           = deriveEncoder[GPrivateProto]
  implicit val encodeGDeployId: Encoder[GDeployIdProto]         = deriveEncoder[GDeployIdProto]
  implicit val encodeGDeployerId: Encoder[GDeployerIdProto]     = deriveEncoder[GDeployerIdProto]
  implicit val encodeGSysAuthToken: Encoder[GSysAuthTokenProto] = deriveEncoder[GSysAuthTokenProto]
  implicit val encodeUnfInstance: Encoder[UnfInstance]          = deriveEncoder[UnfInstance]
  implicit val encodeGUnforgeable: Encoder[GUnforgeableProto]   = deriveEncoder[GUnforgeableProto]
  implicit val encodeBundle: Encoder[BundleProto]               = deriveEncoder[BundleProto]
  implicit val encodeVarRef: Encoder[VarRefProto]               = deriveEncoder[VarRefProto]
  implicit val encodeConnectiveBody: Encoder[ConnectiveBodyProto] =
    deriveEncoder[ConnectiveBodyProto]
  implicit val encodeConnectiveInstance: Encoder[ConnectiveInstance] =
    deriveEncoder[ConnectiveInstance]
  implicit val encodeConnective: Encoder[ConnectiveProto] = deriveEncoder[ConnectiveProto]
  implicit val encodeAlwaysEqual: Encoder[AlwaysEqual[BitSet]] =
    Encoder.encodeUnit.contramap[AlwaysEqual[BitSet]](_ => ())

  // FIXME blake2b512Random encode. Question Is that really neccessary?
  implicit val encodeBlake2b512Random: Encoder[Blake2b512Random] =
    Encoder.encodeUnit.contramap[Blake2b512Random](_ => ())

  implicit val decodeByteString: Decoder[ByteString] =
    Decoder.decodeString.map[ByteString](s => s.unsafeHexToByteString)
  implicit val decodeBondInfo: Decoder[BondInfo] = deriveDecoder[BondInfo]
  implicit val decodeJustificationInfo: Decoder[JustificationInfo] =
    deriveDecoder[JustificationInfo]
  implicit val decodeLightBlockInfo: Decoder[LightBlockInfo] = deriveDecoder[LightBlockInfo]
  implicit val decodePar: Decoder[ParProto]                  = deriveDecoder[ParProto]
  implicit val decodeSend: Decoder[SendProto]                = deriveDecoder[SendProto]
  implicit val decodeWildcardMsg: Decoder[WildcardMsgProto]  = deriveDecoder[WildcardMsgProto]
  implicit val decodeVarInstance: Decoder[VarInstance]       = deriveDecoder[VarInstance]
  implicit val decodeVar: Decoder[VarProto]                  = deriveDecoder[VarProto]
  implicit val decodeReceiveBind: Decoder[ReceiveBindProto]  = deriveDecoder[ReceiveBindProto]
  implicit val decodeReceive: Decoder[ReceiveProto]          = deriveDecoder[ReceiveProto]
  implicit val decodeNew: Decoder[NewProto]                  = deriveDecoder[NewProto]
  implicit val decodeENot: Decoder[ENotProto]                = deriveDecoder[ENotProto]
  implicit val decodeENeg: Decoder[ENegProto]                = deriveDecoder[ENegProto]
  implicit val decodeEMult: Decoder[EMultProto]              = deriveDecoder[EMultProto]
  implicit val decodeEDiv: Decoder[EDivProto]                = deriveDecoder[EDivProto]
  implicit val decodeEPlus: Decoder[EPlusProto]              = deriveDecoder[EPlusProto]
  implicit val decodeEMinus: Decoder[EMinusProto]            = deriveDecoder[EMinusProto]
  implicit val decodeELt: Decoder[ELtProto]                  = deriveDecoder[ELtProto]
  implicit val decodeELte: Decoder[ELteProto]                = deriveDecoder[ELteProto]
  implicit val decodeEGt: Decoder[EGtProto]                  = deriveDecoder[EGtProto]
  implicit val decodeEGte: Decoder[EGteProto]                = deriveDecoder[EGteProto]
  implicit val decodeEEq: Decoder[EEqProto]                  = deriveDecoder[EEqProto]
  implicit val decodeENeq: Decoder[ENeqProto]                = deriveDecoder[ENeqProto]
  implicit val decodeEAnd: Decoder[EAndProto]                = deriveDecoder[EAndProto]
  implicit val decodeEOr: Decoder[EOrProto]                  = deriveDecoder[EOrProto]
  implicit val decodeEShortAnd: Decoder[EShortAndProto]      = deriveDecoder[EShortAndProto]
  implicit val decodeEShortOr: Decoder[EShortOrProto]        = deriveDecoder[EShortOrProto]
  implicit val decodeEVar: Decoder[EVarProto]                = deriveDecoder[EVarProto]
  implicit val decodeEList: Decoder[EListProto]              = deriveDecoder[EListProto]
  implicit val decodeETuple: Decoder[ETupleProto]            = deriveDecoder[ETupleProto]
  implicit val decodeParSet: Decoder[ParSetProto] =
    Decoder.decodeList[ParProto].map[ParSetProto](p => ParSetProto(p, None))
  implicit val decodeParMap: Decoder[ParMapProto] =
    Decoder.decodeList[(ParProto, ParProto)].map[ParMapProto](m => ParMapProto(m))
  implicit val decodeEMethod: Decoder[EMethodProto]   = deriveDecoder[EMethodProto]
  implicit val decodeEMatches: Decoder[EMatchesProto] = deriveDecoder[EMatchesProto]
  implicit val decodeEPercentPercent: Decoder[EPercentPercentProto] =
    deriveDecoder[EPercentPercentProto]
  implicit val decodeEPlusPlus: Decoder[EPlusPlusProto]         = deriveDecoder[EPlusPlusProto]
  implicit val decodeEMinusMinus: Decoder[EMinusMinusProto]     = deriveDecoder[EMinusMinusProto]
  implicit val decodeEMod: Decoder[EModProto]                   = deriveDecoder[EModProto]
  implicit val decodeExprInstance: Decoder[ExprInstance]        = deriveDecoder[ExprInstance]
  implicit val decodeExpr: Decoder[ExprProto]                   = deriveDecoder[ExprProto]
  implicit val decodeMatchCase: Decoder[MatchCaseProto]         = deriveDecoder[MatchCaseProto]
  implicit val decodeMatch: Decoder[MatchProto]                 = deriveDecoder[MatchProto]
  implicit val decodeGPrivate: Decoder[GPrivateProto]           = deriveDecoder[GPrivateProto]
  implicit val decodeGDeployId: Decoder[GDeployIdProto]         = deriveDecoder[GDeployIdProto]
  implicit val decodeGDeployerId: Decoder[GDeployerIdProto]     = deriveDecoder[GDeployerIdProto]
  implicit val decodeGSysAuthToken: Decoder[GSysAuthTokenProto] = deriveDecoder[GSysAuthTokenProto]
  implicit val decodeUnfInstance: Decoder[UnfInstance]          = deriveDecoder[UnfInstance]
  implicit val decodeGUnforgeable: Decoder[GUnforgeableProto]   = deriveDecoder[GUnforgeableProto]
  implicit val decodeBundle: Decoder[BundleProto]               = deriveDecoder[BundleProto]
  implicit val decodeVarRef: Decoder[VarRefProto]               = deriveDecoder[VarRefProto]
  implicit val decodeConnectiveBody: Decoder[ConnectiveBodyProto] =
    deriveDecoder[ConnectiveBodyProto]
  implicit val decodeConnectiveInstance: Decoder[ConnectiveInstance] =
    deriveDecoder[ConnectiveInstance]
  implicit val decodeConnective: Decoder[ConnectiveProto] = deriveDecoder[ConnectiveProto]
  implicit val decodeAlwaysEqual: Decoder[AlwaysEqual[BitSet]] =
    Decoder.decodeUnit.map[AlwaysEqual[BitSet]](_ => AlwaysEqual(BitSet()))

  // FIXME blake2b512Random decode. Question Is that really neccessary?
  implicit val decodeDummyBlake2b512Random: Decoder[Blake2b512Random] =
    Decoder.decodeUnit.map[Blake2b512Random](_ => Blake2b512Random.defaultRandom)
}
