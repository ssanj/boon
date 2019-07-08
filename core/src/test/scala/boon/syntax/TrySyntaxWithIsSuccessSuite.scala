package boon
package syntax

import boon.model.internal._
import `try`._
import scala.util.Try

object TrySyntaxWithIsSuccessSuite extends SuiteLike("Try Syntax isSuccess Suite") {

  private val t1 = test("Try Syntax isSuccess fails on Failure") {
    val failOnFailureTest = test("fails on Failure") {
      isSuccess(Try("five".toInt))
    }

    singleTestFailed(
      testName      = "fails on Failure",
      assertionName = "is Success",
      context       = Map("value" -> "Failure(java.lang.NumberFormatException(For input string: \"five\"))"),
      location      = 12,
      error         = "expected Success got: Failure(java.lang.NumberFormatException(For input string: \"five\"))"
    )(Boon.runTest(failOnFailureTest))
  }

  private val t2 = test("Try Syntax isSuccess passes on Success") {
    val passOnSuccessTest = test("passes on Success") {
      isSuccess(Try("5".toInt))
    }

    singleTestPassed(
      testName      = "passes on Success",
      assertionName = "is Success",
      context       = Map("value" -> "Success(5)"),
      location      = 26,
    )(Boon.runTest(passOnSuccessTest))
  }

  override val tests = oneOrMore(t1, t2)
}