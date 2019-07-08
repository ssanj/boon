package boon
package syntax

import boon.model.internal._
import `try`._
import scala.util.Try

object TrySyntaxWithIsFailureSuite extends SuiteLike("Try Syntax isFailure Suite") {

  private val t1 = test("Try Syntax isFailure fails on Success") {
    val failOnSuccessTest = test("fails on Success") {
      isFailure(Try("5".toInt))
    }

    singleTestFailed(
      testName      = "fails on Success",
      assertionName = "is Failure",
      context       = Map("value" -> "Success(5)"),
      location      = 12,
      error         = "expected Failure got: Success(5)"
    )(Boon.runTest(failOnSuccessTest))
  }

  private val t2 = test("Try Syntax isFailure passes on Failure") {
    val passOnSuccessTest = test("passes on Failure") {
      isFailure(Try("five".toInt))
    }

    singleTestPassed(
      testName      = "passes on Failure",
      assertionName = "is Failure",
      context       = Map("value" -> "Failure(java.lang.NumberFormatException(For input string: \"five\"))"),
      location      = 26,
    )(Boon.runTest(passOnSuccessTest))
  }

  override val tests = oneOrMore(t1, t2)
}