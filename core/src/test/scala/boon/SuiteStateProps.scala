package boon

import boon.model.TestResult
import boon.model.DeferredSuite
import boon.model.SuiteState
import boon.model.SuiteResult
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.BooleanOperators
import scalacheck.ModelArb._

object SuiteStateProps extends Properties("SuiteState") {

  private case class OnlySuccessfulSuiteResult(suiteResult: SuiteResult)
  private case class OnlyUnsuccessfulSuiteResult(suiteResult: SuiteResult)
  private case class MixedSuiteResult(suiteResult: SuiteResult)

  property("Fail when all tests have failed or thrown") =
    Prop.forAll { (sr: OnlyUnsuccessfulSuiteResult) =>
      val suiteState  = SuiteResult.suiteResultToSuiteState(sr.suiteResult)
      (suiteState == SuiteState.Failed) :| s"Expected SuiteState to be 'Failed' but got: '${suiteState}'"
    }

  property("Fail when there are passed and failed tests") =
    Prop.forAll { (sr: MixedSuiteResult) =>
      val suiteState  = SuiteResult.suiteResultToSuiteState(sr.suiteResult)
      (suiteState == SuiteState.Failed) :| s"Expected SuiteState to be 'Failed' but got: '${suiteState}'"
    }

  property("Pass when all tests have passed or been ignored") =
    Prop.forAll { (sr: OnlySuccessfulSuiteResult) =>
      val suiteState  = SuiteResult.suiteResultToSuiteState(sr.suiteResult)
      (suiteState == SuiteState.Passed) :| s"Expected SuiteState to be 'Passed' but got: '${suiteState}'"
    }

  private implicit val onlySuccessfulSuiteResultArbitrary: Arbitrary[OnlySuccessfulSuiteResult] = Arbitrary {
    for {
      suite       <- arbitrary[DeferredSuite]
      testResults <- manyOf(10, onlySuccessfulTestResultGen)
    } yield OnlySuccessfulSuiteResult(SuiteResult(suite, testResults))
  }

  private implicit val onlyUnsuccessfulSuiteResultArbitrary: Arbitrary[OnlyUnsuccessfulSuiteResult] = Arbitrary {
    for {
      suite       <- arbitrary[DeferredSuite]
      testResults <- manyOf(10, onlyUnsuccessfulTestResultGen)
    } yield OnlyUnsuccessfulSuiteResult(SuiteResult(suite, testResults))
  }

  private implicit val mixedSuiteResultArbitrary: Arbitrary[MixedSuiteResult] = Arbitrary {
    for {
       suite        <- arbitrary[DeferredSuite]
       unsuccessful <- onlyUnsuccessfulTestResultGen
       successful   <- onlySuccessfulTestResultGen
       testResults  <- manyOf(10, Gen.oneOf(onlySuccessfulTestResultGen, onlyUnsuccessfulTestResultGen))
    } yield MixedSuiteResult(SuiteResult(suite, unsuccessful +: successful +: testResults))
  }

  private def onlyUnsuccessfulTestResultGen: Gen[TestResult] =
    Gen.oneOf(testThrewResultGen,
             compositeTestStoppedOnFirstTestResultGen,
             singleTestAllFailedTestResultGen)

  private def onlySuccessfulTestResultGen: Gen[TestResult] =
    Gen.oneOf(testIgnoredResultGen,
             compositeTestAllPassedTestResultGen,
             singleTestAllPassedTestResultGen)

}