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

object SuiteStateProps extends Properties("SuiteState") {

  property("Failed when all tests have failed or thrown") =
    Prop.forAll(arbitrary[DeferredSuite], manyOf(5, onlyUnsuccessfulTestResultGen)) { (dSuite: DeferredSuite, testResults: NonEmptySeq[TestResult]) =>
      val suiteResult = SuiteResult(dSuite, testResults)
      val suiteState  = SuiteResult.suiteResultToSuiteState(suiteResult)
      (suiteState == SuiteState.Failed) :| s"Expected SuiteState to be 'Failed' but got: '${suiteState}'"
    }

  property("Passed when all tests have passed or been ignored") =
    Prop.forAll(arbitrary[DeferredSuite], manyOf(5, onlySuccessfulTestResultGen)) { (dSuite: DeferredSuite, testResults: NonEmptySeq[TestResult]) =>
      val suiteResult = SuiteResult(dSuite, testResults)
      val suiteState  = SuiteResult.suiteResultToSuiteState(suiteResult)
      (suiteState == SuiteState.Passed) :| s"Expected SuiteState to be 'Passed' but got: '${suiteState}'"
    }
}