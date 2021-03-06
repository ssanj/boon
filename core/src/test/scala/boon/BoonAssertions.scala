package boon

import boon.model.AssertionResultThrew
import boon.model.AssertionThrow
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

  final case class Expected(value: String)
  final case class Got[A](value: A)
  final case class Desc(value: String)

  def failWith[A](expected: Expected, other: => Got[A], assertionDesc: Desc): AssertionData =
    fail(s"Expected ${expected.value} but got ${other.value}") | assertionDesc.value

  def assertAssertionResultPassed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(aName), _, _))) =>
          aName =?= assertionName | s"assertion name of '$assertionName'"
        case other => failWith(Expected("SingleAssertionResult/AssertionResultPassed"), Got(other), Desc("assertion result type"))
      }
    }

  def assertSequentialPass(assertionName: String)(sp: SequentialPass): AssertionData = {
    sp.name.value =?= assertionName | s"assertion name of $assertionName"
  }

  def assertAssertionResultFailed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(aName), _, _, _), _))) =>
          aName =?= assertionName | s"assertion name of $assertionName"
        case other => failWith(Expected("SingleAssertionResult/AssertionResultFailed"), Got(other), Desc("assertion result type"))
      }
    }

  def assertAssertionResultThrew(assertionName: String, f: Throwable => AssertionData)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultThrew(AssertionThrow(AssertionName(aName), throwable, _))) =>
          aName =?= assertionName | s"assertion name of $assertionName" and
          f(throwable)
        case other => failWith(Expected("SingleAssertionResult/AssertionResultThrew"), Got(other), Desc("assertion result type"))
      }
    }
}



