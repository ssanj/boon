package boon

import boon.model.SuiteState
import boon.model.SuiteResult
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Prop.propBoolean
import scalacheck.SuiteResultArb._

object SuiteStateProps extends Properties("SuiteState") {

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
}