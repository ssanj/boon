package boon

import boon.model.TestResult
import boon.model.SuiteState
import boon.model.DeferredSuite
import boon.model.SuiteResult
import boon.result.SuiteOutput
import org.scalacheck.Properties
import org.scalacheck._
import Arbitrary.arbitrary
import org.scalacheck.Prop.BooleanOperators
import scalacheck.ModelArb._

object SuiteOutputProps extends Properties("SuiteOutput") {

  private case class OnlySuccessfulSuiteResult(suiteResult: SuiteResult)
  private case class OnlyUnsuccessfulSuiteResult(suiteResult: SuiteResult)
  private case class MixedSuiteResult(suiteResult: SuiteResult)

  property("fails if any test fails") =
    Prop.forAll { result: OnlyUnsuccessfulSuiteResult =>
        val suiteOutput = SuiteOutput.toSuiteOutput(result.suiteResult)
        (suiteOutput.state == SuiteState.Failed) :| s"expected a failure but got: ${suiteOutput}"
    }

  property("fails if some tests fail") =
    Prop.forAll { result: MixedSuiteResult =>
        val suiteOutput = SuiteOutput.toSuiteOutput(result.suiteResult)
        (suiteOutput.state == SuiteState.Failed) :| s"expected a failure but got: ${suiteOutput}"
    }

  property("pass if all tests pass") =
    Prop.forAll { result: OnlySuccessfulSuiteResult =>
        val suiteOutput = SuiteOutput.toSuiteOutput(result.suiteResult)
        (suiteOutput.state == SuiteState.Passed) :| s"expected a success but got: ${suiteOutput}"
    }

  property("toSuiteOutput is the same as SuiteResult.suiteResultToSuiteState") =
    Prop.forAll { suiteResult: SuiteResult =>
        val suiteOutput = SuiteOutput.toSuiteOutput(suiteResult)
        val suiteState  = SuiteResult.suiteResultToSuiteState(suiteResult)

        (suiteOutput.state == suiteState) :| s"expected a suiteOutput: ${suiteOutput} to be equal to: ${suiteState} for ${suiteResult}"
    }

  private implicit val onlyUnsuccessfulSuiteResultArbitrary: Arbitrary[OnlyUnsuccessfulSuiteResult] = Arbitrary {
    for {
      suite       <- arbitrary[DeferredSuite]
      testResults <- manyOf(10, onlyUnsuccessfulTestResultGen)
    } yield OnlyUnsuccessfulSuiteResult(SuiteResult(suite, testResults))
  }

  private def onlyUnsuccessfulTestResultGen: Gen[TestResult] =
    Gen.oneOf(testThrewResultGen,
             compositeTestStoppedOnFirstTestResultGen,
             singleTestAllFailedTestResultGen)

  private implicit val onlySuccessfulSuiteResultArbitrary: Arbitrary[OnlySuccessfulSuiteResult] = Arbitrary {
    for {
      suite       <- arbitrary[DeferredSuite]
      testResults <- manyOf(10, onlySuccessfulTestResultGen)
    } yield OnlySuccessfulSuiteResult(SuiteResult(suite, testResults))
  }

  private def onlySuccessfulTestResultGen: Gen[TestResult] =
    Gen.oneOf(testIgnoredResultGen,
             compositeTestAllPassedTestResultGen,
             singleTestAllPassedTestResultGen)

  private implicit val mixedSuiteResultArbitrary: Arbitrary[MixedSuiteResult] = Arbitrary {
    for {
       suite        <- arbitrary[DeferredSuite]
       unsuccessful <- onlyUnsuccessfulTestResultGen
       successful   <- onlySuccessfulTestResultGen
       testResults  <- manyOf(10, Gen.oneOf(onlySuccessfulTestResultGen, onlyUnsuccessfulTestResultGen))
    } yield MixedSuiteResult(SuiteResult(suite, unsuccessful +: successful +: testResults))
  }
}