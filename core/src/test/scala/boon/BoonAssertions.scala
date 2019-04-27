package boon

import boon.model.AssertionResultThrew
import boon.model.AssertionThrow
import boon.model.AssertionName
import boon.model.Assertion
import boon.model.AssertionError
import boon.model.AssertionResultFailed
import boon.model.SequentialPass
import boon.model.AssertionResult
import boon.model.AssertionName
import boon.model.AssertionTriple
import boon.model.AssertionResultPassed
import boon.model.SingleAssertionResult
import boon.model.AssertionData

private[boon] object BoonAssertions {

  def failWith[A](expected: String, other: => A, assertionName: String): AssertionData =
    fail(s"Expected $expected but got $other") | assertionName

  def assertAssertionResultPassed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(aName), context, _))) =>
          aName =?= assertionName | s"assertion name of '$assertionName'"
        case other => failWith("SingleAssertionResult/AssertionResultPassed", other, "assertion result type")
      }
    }

  def assertSequentialPass(assertionName: String)(sp: SequentialPass): AssertionData = {
    sp.name.value =?= assertionName | s"assertion name of $assertionName"
  }

  def assertAssertionResultFailed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(aName), _, _, _), errors))) =>
          aName =?= assertionName | s"assertion name of $assertionName"
        case other => failWith("SingleAssertionResult/AssertionResultFailed", other, "assertion result type")
      }
    }

  def assertAssertionResultThrew(assertionName: String, f: Throwable => AssertionData)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultThrew(AssertionThrow(AssertionName(aName), throwable, _))) =>
          aName =?= assertionName | s"assertion name of $assertionName" and
          f(throwable)
        case other => failWith("SingleAssertionResult/AssertionResultThrew", other, "assertion result type")
      }
    }

}