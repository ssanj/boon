package boon

import boon.model.SingleAssertionResult
import boon.model.AssertionResult
import boon.model.AssertionState
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Prop.propBoolean
import scalacheck.ModelArb._

object AssertionStateProps extends Properties("AssertionState") {

  private case class OnlySuccessfulAssertionResult(assertionResult: AssertionResult)
  private case class OnlyUnsuccessfulAssertionResult(assertionResult: AssertionResult)

  property("Failed when any assertion has failed or thrown") =
    Prop.forAll { (ar: OnlyUnsuccessfulAssertionResult) =>
      val assertionState  = AssertionResult.assertionResultToAssertionState(ar.assertionResult)
      (assertionState == AssertionState.Failed) :| s"Expected AssertionState to be 'Failed' but got: '${assertionState}'"
    }

  property("Passed when all assertions have passed") =
    Prop.forAll { (ar: OnlySuccessfulAssertionResult) =>
      val assertionState  = AssertionResult.assertionResultToAssertionState(ar.assertionResult)
      (assertionState == AssertionState.Passed) :| s"Expected AssertionState to be 'Passed' but got: '${assertionState}'"
    }

  private implicit val onlySuccessfulAssertionResultArbitrary: Arbitrary[OnlySuccessfulAssertionResult] = Arbitrary {
    onlySuccessfulAssertionResultGen.map(OnlySuccessfulAssertionResult)
  }

  private implicit val onlyUnsuccessfulSuiteResultArbitrary: Arbitrary[OnlyUnsuccessfulAssertionResult] = Arbitrary {
    onlyUnsuccessfulAssertionResultGen.map(OnlyUnsuccessfulAssertionResult)
  }

  private def onlyUnsuccessfulAssertionResultGen: Gen[AssertionResult] =
    Gen.oneOf(assertionResultFailedGen,
             assertionResultThrewGen).map(SingleAssertionResult)


  private def onlySuccessfulAssertionResultGen: Gen[AssertionResult] =
    assertionResultPassedGen.map(SingleAssertionResult)

}