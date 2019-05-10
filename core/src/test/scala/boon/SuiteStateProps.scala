package boon

import boon.model.TestState
import boon.model.TestResult
import boon.model.SuiteState
import boon.model.SuiteResult
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Prop.BooleanOperators
import scalacheck.ModelArb._

object SuiteStateProps extends Properties("SuiteState") {

  property("Failed if at least one test fails") =
    Prop.forAll { suiteResult: SuiteResult =>
      val suiteState = SuiteResult.suiteResultToSuiteState(suiteResult)
      val anyTestHasFailed = suiteResult.testResults.exists(tr => TestResult.testResultToTestState(tr) == TestState.Failed)

      if (anyTestHasFailed) (suiteState == SuiteState.Failed) :| s"Expected SuiteState to be 'Failed' but got: '${suiteState}'"
      else Prop(true)
    }

  property("Pass if there are no test fails") =
    Prop.forAll { suiteResult: SuiteResult =>
      val suiteState = SuiteResult.suiteResultToSuiteState(suiteResult)
      val anyTestHasFailed = suiteResult.testResults.exists(tr => TestResult.testResultToTestState(tr) == TestState.Failed)
      if (!anyTestHasFailed) (suiteState == SuiteState.Passed) :| s"Expected SuiteState to be 'Passed' but got: '${suiteState}'"
      else Prop(true)
    }
}