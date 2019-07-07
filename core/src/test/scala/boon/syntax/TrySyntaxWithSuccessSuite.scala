package boon
package syntax

import boon.model.internal._
import `try`._
import scala.util.Try

object TrySyntaxWithSuccessSuite extends SuiteLike("Try Syntax success_? Suite") {

  private val t1 = test("Try Syntax success_? fails on Failure") {
    val failOnFailureTest = test("fails on Failure") {
      success_?(Try("five".toInt))(_ =?= 5 | "five")
    }

    singleTestFailed(
      testName      = "fails on Failure",
      assertionName = "expected Success",
      context       = Map("value" -> "Failure(java.lang.NumberFormatException(For input string: \"five\"))"),
      location      = 12,
      error         = "expected Success got: Failure(java.lang.NumberFormatException(For input string: \"five\"))"
    )(Boon.runTest(failOnFailureTest))
  }

  private val t2 = test("Try Syntax success_? passes on Success") {
    val passOnSuccessTest = test("passes on Success") {
      success_?(Try("5".toInt))(_ =?= 5 | "five")
    }

    singleTestPassed(
      testName      = "passes on Success",
      assertionName = "five",
      context       = Map("value" -> "Success(5)"),
      location      = 26,
    )(Boon.runTest(passOnSuccessTest))
  }

  private val t3 = test("Try Syntax success_? fails on Assertion") {
    val failOnSuccessTest = test("fails on Assertion") {
      success_?(Try("5".toInt))(_ =?= 6 | "five")
    }

    singleTestFailed(
      testName      = "fails on Assertion",
      assertionName = "five",
      context       = Map("value" -> "Success(5)"),
      location      = 39,
      error         = "5 != 6"
    )(Boon.runTest(failOnSuccessTest))
  }

  override val tests = oneOrMore(t1, t2, t3)
}