package boon.scalacheck

import boon.model.FailableAssertion
import boon.model.FailedAssertion
import boon.model.PassedAssertion
import boon.model.stats.AssertionCount
import boon.model.stats.SuiteStats
import boon.model.stats.StatusCount

import org.scalacheck._
import Arbitrary.arbitrary

object Arb {

  implicit val failableAssertionArb: Arbitrary[FailableAssertion] = Arbitrary {
    for {
      message           <- arbitrary[String]
      failableAssertion <- Gen.oneOf[FailableAssertion](FailedAssertion(message), PassedAssertion)
    } yield failableAssertion
  }

  implicit val statusCountArb: Arbitrary[StatusCount] = Arbitrary {
    for {
      passed <- Gen.posNum[Int]
      failed <- Gen.posNum[Int]
    } yield StatusCount(passed, failed)
  }

  implicit val assertionCountArb: Arbitrary[AssertionCount] = Arbitrary {
    for {
      sc     <- arbitrary[StatusCount]
      notRun <- Gen.posNum[Int]
    } yield AssertionCount(sc, notRun)
  }

  implicit val suiteStatsCountArb: Arbitrary[SuiteStats] = Arbitrary {
    for {
      suites     <- arbitrary[StatusCount]
      tests     <- arbitrary[StatusCount]
      assertions <- arbitrary[AssertionCount]
    } yield SuiteStats(suites, tests, assertions)
  }
}
