package boon.scalacheck

import boon.model.TestResult
import boon.model.DeferredSuite
import boon.model.SuiteResult
import org.scalacheck._
import Arbitrary.arbitrary
import ModelArb._

object SuiteResultArb {

  final case class OnlySuccessfulSuiteResult(suiteResult: SuiteResult)
  final case class OnlyUnsuccessfulSuiteResult(suiteResult: SuiteResult)
  final case class MixedSuiteResult(suiteResult: SuiteResult)

  implicit val onlyUnsuccessfulSuiteResultArbitrary: Arbitrary[OnlyUnsuccessfulSuiteResult] = Arbitrary {
    for {
      suite       <- arbitrary[DeferredSuite]
      testResults <- manyOf(10, onlyUnsuccessfulTestResultGen)
    } yield OnlyUnsuccessfulSuiteResult(SuiteResult(suite, testResults))
  }

  def onlyUnsuccessfulTestResultGen: Gen[TestResult] =
    Gen.oneOf(testThrewResultGen,
             compositeTestStoppedOnFirstTestResultGen,
             singleTestAllFailedTestResultGen)

  implicit val onlySuccessfulSuiteResultArbitrary: Arbitrary[OnlySuccessfulSuiteResult] = Arbitrary {
    for {
      suite       <- arbitrary[DeferredSuite]
      testResults <- manyOf(10, onlySuccessfulTestResultGen)
    } yield OnlySuccessfulSuiteResult(SuiteResult(suite, testResults))
  }

  def onlySuccessfulTestResultGen: Gen[TestResult] =
    Gen.oneOf(testIgnoredResultGen,
             compositeTestAllPassedTestResultGen,
             singleTestAllPassedTestResultGen)

  implicit val mixedSuiteResultArbitrary: Arbitrary[MixedSuiteResult] = Arbitrary {
    for {
       suite        <- arbitrary[DeferredSuite]
       unsuccessful <- onlyUnsuccessfulTestResultGen
       successful   <- onlySuccessfulTestResultGen
       testResults  <- manyOf(10, Gen.oneOf(onlySuccessfulTestResultGen, onlyUnsuccessfulTestResultGen))
    } yield MixedSuiteResult(SuiteResult(suite, unsuccessful +: successful +: testResults))
  }
}