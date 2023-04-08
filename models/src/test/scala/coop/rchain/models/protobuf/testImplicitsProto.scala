package coop.rchain.models.protobuf

import com.google.protobuf.ByteString
import coop.rchain.models.AlwaysEqual
import monix.eval.Coeval
import org.scalacheck.ScalacheckShapeless._
import org.scalacheck.{Arbitrary, Gen, Shrink}

import scala.collection.immutable.BitSet

object testImplicitsProto {
  val genBitSet = for { bitMask <- Arbitrary.arbitrary[Array[Long]] } yield BitSet.fromBitMask(
    bitMask
  )
  implicit val arbBitSet: Arbitrary[BitSet] = Arbitrary(genBitSet)

  //FIXME this is broken, and makes our tests blind for mishandling Option
  // The ScalaPB generators have quirks in which required objects
  // become Options. See https://github.com/scalapb/ScalaPB/issues/40 .
  // We override so that we cannot get None for these required objects.
  implicit def arbOption[T](implicit a: Arbitrary[T]): Arbitrary[Option[T]] =
    Arbitrary(for { s <- a.arbitrary } yield Some(s))

  implicit val arbByteArray: Arbitrary[ByteString] =
    Arbitrary(Arbitrary.arbitrary[Array[Byte]].map(ba => ByteString.copyFrom(ba)))

  implicit val arbSortedParMap: Arbitrary[SortedParMapProto] = Arbitrary(for {
    ps <- Arbitrary.arbitrary[Seq[(ParProto, ParProto)]]
  } yield SortedParMapProto(ps))

  implicit def coeval[A: Arbitrary]: Arbitrary[Coeval[A]] =
    Arbitrary(Arbitrary.arbitrary[A].map(a => Coeval.delay(a)))

  //Par and Expr (or Par at least) need to be first here, or else the compiler dies terribly.
  implicit val ParArbitrary                = implicitly[Arbitrary[ParProto]]
  implicit val ExprArbitrary               = implicitly[Arbitrary[ExprProto]]
  implicit val BindPatternArbitrary        = implicitly[Arbitrary[BindPatternProto]]
  implicit val BundleArbitrary             = implicitly[Arbitrary[BundleProto]]
  implicit val ConnectiveArbitrary         = implicitly[Arbitrary[ConnectiveProto]]
  implicit val ConnectiveBodyArbitrary     = implicitly[Arbitrary[ConnectiveBodyProto]]
  implicit val EListArbitrary              = implicitly[Arbitrary[EListProto]]
  implicit val EMapArbitrary               = implicitly[Arbitrary[EMapProto]]
  implicit val EMatchesArbitrary           = implicitly[Arbitrary[EMatchesProto]]
  implicit val EMethodArbitrary            = implicitly[Arbitrary[EMethodProto]]
  implicit val ENeqArbitrary               = implicitly[Arbitrary[ENeqProto]]
  implicit val ENotArbitrary               = implicitly[Arbitrary[ENotProto]]
  implicit val EOrArbitrary                = implicitly[Arbitrary[EOrProto]]
  implicit val ESetArbitrary               = implicitly[Arbitrary[ESetProto]]
  implicit val ETupleArbitrary             = implicitly[Arbitrary[ETupleProto]]
  implicit val EVarArbitrary               = implicitly[Arbitrary[EVarProto]]
  implicit val GUnforgeableArbitrary       = implicitly[Arbitrary[GUnforgeableProto]]
  implicit val GPrivateArbitrary           = implicitly[Arbitrary[GPrivateProto]]
  implicit val GDeployerIdArbitrary        = implicitly[Arbitrary[GDeployerIdProto]]
  implicit val KeyValuePairArbitrary       = implicitly[Arbitrary[KeyValuePairProto]]
  implicit val ListBindPatternsArbitrary   = implicitly[Arbitrary[ListBindPatternsProto]]
  implicit val MatchArbitrary              = implicitly[Arbitrary[MatchProto]]
  implicit val MatchCaseArbitrary          = implicitly[Arbitrary[MatchCaseProto]]
  implicit val NewArbitrary                = implicitly[Arbitrary[NewProto]]
  implicit val ParWithRandomArbitrary      = implicitly[Arbitrary[ParWithRandomProto]]
  implicit val ListParWithRandomArbitrary  = implicitly[Arbitrary[ListParWithRandomProto]]
  implicit val PCostArbitrary              = implicitly[Arbitrary[PCostProto]]
  implicit val ReceiveArbitrary            = implicitly[Arbitrary[ReceiveProto]]
  implicit val ReceiveBindArbitrary        = implicitly[Arbitrary[ReceiveBindProto]]
  implicit val SendArbitrary               = implicitly[Arbitrary[SendProto]]
  implicit val TaggedContinuationArbitrary = implicitly[Arbitrary[TaggedContinuationProto]]
  implicit val VarArbitrary                = implicitly[Arbitrary[VarProto]]
  implicit val VarRefArbitrary             = implicitly[Arbitrary[VarRefProto]]
  implicit val ParSetArbitrary             = implicitly[Arbitrary[ParSetProto]]
  implicit val ParMapArbitrary             = implicitly[Arbitrary[ParMapProto]]

  implicit val ParShrink                = implicitly[Shrink[ParProto]]
  implicit val ExprShrink               = implicitly[Shrink[ExprProto]]
  implicit val BindPatternShrink        = implicitly[Shrink[BindPatternProto]]
  implicit val BundleShrink             = implicitly[Shrink[BundleProto]]
  implicit val ConnectiveShrink         = implicitly[Shrink[ConnectiveProto]]
  implicit val ConnectiveBodyShrink     = implicitly[Shrink[ConnectiveBodyProto]]
  implicit val EListShrink              = implicitly[Shrink[EListProto]]
  implicit val EMapShrink               = implicitly[Shrink[EMapProto]]
  implicit val EMatchesShrink           = implicitly[Shrink[EMatchesProto]]
  implicit val EMethodShrink            = implicitly[Shrink[EMethodProto]]
  implicit val ENeqShrink               = implicitly[Shrink[ENeqProto]]
  implicit val ENotShrink               = implicitly[Shrink[ENotProto]]
  implicit val EOrShrink                = implicitly[Shrink[EOrProto]]
  implicit val ESetShrink               = implicitly[Shrink[ESetProto]]
  implicit val ETupleShrink             = implicitly[Shrink[ETupleProto]]
  implicit val EVarShrink               = implicitly[Shrink[EVarProto]]
  implicit val GUnforgeableShrink       = implicitly[Shrink[GUnforgeableProto]]
  implicit val GPrivateShrink           = implicitly[Shrink[GPrivateProto]]
  implicit val GDeployerIdShrink        = implicitly[Shrink[GDeployerIdProto]]
  implicit val KeyValuePairShrink       = implicitly[Shrink[KeyValuePairProto]]
  implicit val ListBindPatternsShrink   = implicitly[Shrink[ListBindPatternsProto]]
  implicit val MatchShrink              = implicitly[Shrink[MatchProto]]
  implicit val MatchCaseShrink          = implicitly[Shrink[MatchCaseProto]]
  implicit val NewShrink                = implicitly[Shrink[NewProto]]
  implicit val ParWithRandomShrink      = implicitly[Shrink[ParWithRandomProto]]
  implicit val ListParWithRandomShrink  = implicitly[Shrink[ListParWithRandomProto]]
  implicit val PCostShrink              = implicitly[Shrink[PCostProto]]
  implicit val ReceiveShrink            = implicitly[Shrink[ReceiveProto]]
  implicit val ReceiveBindShrink        = implicitly[Shrink[ReceiveBindProto]]
  implicit val SendShrink               = implicitly[Shrink[SendProto]]
  implicit val TaggedContinuationShrink = implicitly[Shrink[TaggedContinuationProto]]
  implicit val VarShrink                = implicitly[Shrink[VarProto]]
  implicit val VarRefShrink             = implicitly[Shrink[VarRefProto]]
  implicit val ParSetShrink             = implicitly[Shrink[ParSetProto]]
  implicit val ParMapShrink             = implicitly[Shrink[ParMapProto]]

  implicit def alwaysEqualArbitrary[A: Arbitrary]: Arbitrary[AlwaysEqual[A]] =
    Arbitrary(Arbitrary.arbitrary[A].map(AlwaysEqual(_)))

  implicit val arbParTreeset: Arbitrary[SortedParHashSetProto] =
    Arbitrary(
      Arbitrary
        .arbitrary[Seq[ParProto]]
        .map(pars => SortedParHashSetProto(pars))
    )

  implicit def arbParTupleSeq: Arbitrary[Seq[(ParProto, ParProto)]] =
    Arbitrary(Gen.listOf(Gen.zip(ParArbitrary.arbitrary, ParArbitrary.arbitrary)))
}
