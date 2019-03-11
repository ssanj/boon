package boon.scalacheck

import boon.model.FailableAssertion
import boon.model.FailedAssertion
import boon.model.PassedAssertion
import org.scalacheck._
import Arbitrary.arbitrary

object Arb {

  implicit val failableAssertionArb: Arbitrary[FailableAssertion] = Arbitrary{
    for {
      message           <- arbitrary[String]
      failableAssertion <- Gen.oneOf[FailableAssertion](FailedAssertion(message), PassedAssertion)
    } yield failableAssertion
  }
}
