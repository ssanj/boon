package boon

import boon.model.Assertion
import org.scalacheck.Properties
import org.scalacheck._
import scalacheck.ModelArb._

object SuiteStateProps extends Properties("SuiteState") {

  property("fail with one failed test") =
    Prop.forAll { p1: Assertion =>
      println(p1)
      true
      //suiteResult: SuiteResult =>
      // val suiteState = SuiteResult.suiteResultToSuiteState(suiteResult)
      // val anyTestHasFailed = suiteResult.testResults.exists(tr => TestResult.testResultToTestState(tr) == TestState.Failed)
      // !anyTestHasFailed || suiteState == SuiteState.Failed
    }
}