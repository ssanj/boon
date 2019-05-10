package boon

import boon.data.NonEmptySeq
import boon.model.TestResult
import boon.model.DeferredSuite
import boon.model.TestResult
import boon.model.SuiteState
import boon.model.SuiteResult
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.BooleanOperators
import scalacheck.ModelArb._
import scalacheck.DataArb.shuffle

//TODO: Supply fully configured data sets from Gen
object SuiteStateProps extends Properties("SuiteState") {

  property("Fail when all tests have failed or thrown") =
    Prop.forAll(arbitrary[DeferredSuite], manyOf(10, onlyUnsuccessfulTestResultGen)) {
      (dSuite: DeferredSuite, testResults: NonEmptySeq[TestResult]) =>
      val suiteResult = SuiteResult(dSuite, testResults)
      val suiteState  = SuiteResult.suiteResultToSuiteState(suiteResult)
      (suiteState == SuiteState.Failed) :| s"Expected SuiteState to be 'Failed' but got: '${suiteState}'"
    }

  property("Fail when there are passed and failed tests") =
    Prop.forAll(arbitrary[DeferredSuite], manyOf(10, onlyUnsuccessfulTestResultGen), manyOf(10, onlySuccessfulTestResultGen)) {
      (dSuite: DeferredSuite, unsuccessfulTestResults: NonEmptySeq[TestResult], successfulTestResults: NonEmptySeq[TestResult]) =>
      val suiteResult = SuiteResult(dSuite, successfulTestResults.concat(unsuccessfulTestResults))
      val suiteState  = SuiteResult.suiteResultToSuiteState(suiteResult)
      (suiteState == SuiteState.Failed) :| s"Expected SuiteState to be 'Failed' but got: '${suiteState}'"
    }

  property("Pass when all tests have passed or been ignored") =
    Prop.forAll(arbitrary[DeferredSuite], manyOf(10, onlySuccessfulTestResultGen)) {
      (dSuite: DeferredSuite, testResults: NonEmptySeq[TestResult]) =>
      val suiteResult = SuiteResult(dSuite, testResults)
      val suiteState  = SuiteResult.suiteResultToSuiteState(suiteResult)
      (suiteState == SuiteState.Passed) :| s"Expected SuiteState to be 'Passed' but got: '${suiteState}'"
    }

  property("Fail when at least a single test fails") =
    Prop.forAll(arbitrary[DeferredSuite], manyOf(10, onlySuccessfulTestResultGen), onlyUnsuccessfulTestResultGen) {
      (dSuite: DeferredSuite, successTestResults: NonEmptySeq[TestResult], failedTestResult: TestResult) =>
      val suiteResult = SuiteResult(dSuite, shuffle(failedTestResult +: successTestResults))
      val suiteState  = SuiteResult.suiteResultToSuiteState(suiteResult)
      (suiteState == SuiteState.Failed) :| s"Expected SuiteState to be 'Failed' but got: '${suiteState}'"
    }
}