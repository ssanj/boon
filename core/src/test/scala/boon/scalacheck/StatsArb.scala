package boon
package scalacheck

import boon.model.stats.AssertionCount
import boon.model.stats.SuiteStats
import boon.model.stats.StatusCount
import boon.model.stats.TestCount

import org.scalacheck._
import Arbitrary.arbitrary

object StatsArb {

  implicit val statusCountArb: Arbitrary[StatusCount] = Arbitrary {
    for {
      passed <- Gen.posNum[Int]
      failed <- Gen.posNum[Int]
    } yield StatusCount(passed, failed)
  }

  implicit val testCountArb: Arbitrary[TestCount] = Arbitrary {
    for {
      sc      <- arbitrary[StatusCount]
      ignored <- Gen.posNum[Int]
    } yield TestCount(sc, ignored)
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
      tests      <- arbitrary[TestCount]
      assertions <- arbitrary[AssertionCount]
    } yield SuiteStats(suites, tests, assertions)
  }
}
