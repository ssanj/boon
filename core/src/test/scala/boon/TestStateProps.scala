package boon

import boon.model.TestResult
import boon.model.TestState
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Prop.propBoolean
import scalacheck.ModelArb._

object TestStateProps extends Properties("TestState") {

  private case class OnlySuccessfulTestResult(testResult: TestResult)
  private case class OnlyUnsuccessfulTestResult(testResult: TestResult)
  private case class OnlyIgnoredTestResult(testResult: TestResult)

  property("Failed when any assertion has failed or thrown") =
    Prop.forAll { (tr: OnlyUnsuccessfulTestResult) =>
      val testState  = TestResult.testResultToTestState(tr.testResult)
      (testState == TestState.Failed) :| s"Expected TestState to be 'Failed' but got: '${testState}'"
    }

  property("Passed when all assertions have passed") =
    Prop.forAll { (tr: OnlySuccessfulTestResult) =>
      val testState  = TestResult.testResultToTestState(tr.testResult)
      (testState == TestState.Passed) :| s"Expected TestState to be 'Passed' but got: '${testState}'"
    }

  property("Ignored when a test is ignored") =
    Prop.forAll { (tr: OnlyIgnoredTestResult) =>
      val testState  = TestResult.testResultToTestState(tr.testResult)
      (testState == TestState.Ignored) :| s"Expected TestState to be 'Ignored' but got: '${testState}'"
    }

  private implicit val onlySuccessfulTestResultArbitrary: Arbitrary[OnlySuccessfulTestResult] = Arbitrary {
    onlySuccessfulTestResultGen.map(OnlySuccessfulTestResult)
  }

  private implicit val onlyUnsuccessfulSuiteResultArbitrary: Arbitrary[OnlyUnsuccessfulTestResult] = Arbitrary {
    onlyUnsuccessfulTestResultGen.map(OnlyUnsuccessfulTestResult)
  }

  private implicit val onlyIgnoredSuiteResultArbitrary: Arbitrary[OnlyIgnoredTestResult] = Arbitrary {
    testIgnoredResultGen.map(OnlyIgnoredTestResult)
  }

  private def onlyUnsuccessfulTestResultGen: Gen[TestResult] =
    Gen.oneOf(testThrewResultGen,
             compositeTestStoppedOnFirstTestResultGen,
             singleTestAllFailedTestResultGen)

  private def onlySuccessfulTestResultGen: Gen[TestResult] =
    Gen.oneOf(compositeTestAllPassedTestResultGen,
             singleTestAllPassedTestResultGen)

}