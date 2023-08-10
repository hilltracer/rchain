package coop.rchain.models.rholangn

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class TestLazy extends AnyFlatSpec with ScalaCheckPropertyChecks with Matchers {
  it should "test lazy for serializedSize" in {
    val p    = NilN
    val list = EListN(Seq(p, p, p, p))
    val size = list.serializedSize
    println("Computed size is " + size)
    true should be(true)
  }
}
