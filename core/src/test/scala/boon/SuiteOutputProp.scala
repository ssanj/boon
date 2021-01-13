package boon

import boon.model.SuiteState
import boon.model.SuiteResult
import boon.result.SuiteOutput
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Prop.propBoolean
import scalacheck.ModelArb._
import scalacheck.SuiteResultArb._

object SuiteOutputProps extends Properties("SuiteOutput") {

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
}