package coop.rchain.models.rholangn

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.util.Random

class SortingSpec extends AnyFlatSpec with ScalaCheckPropertyChecks with Matchers {

  it should "test that ParProc, ESet, EMap sort the data in the same way regardless of input order" in {
    val original: Seq[GIntN] = (1 to 10).map(x => GIntN(x.toLong))

    (1 to 10).foreach { _ => // run test 10 times with different shuffle orders
      val shuffled = Random.shuffle(original)

      val parProcRes = ParProcN(shuffled).sortedPs
      val eSetRes    = ESetN(shuffled).sortedPs
      val eMapRes    = EMapN(shuffled.map((_, NilN))).keys

      eSetRes should be(parProcRes)
      eMapRes should be(parProcRes)
    }
  }
}
