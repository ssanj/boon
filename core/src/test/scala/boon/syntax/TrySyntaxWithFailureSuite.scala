package boon
package syntax

import boon.model.internal._
import `try`._
import scala.util.Try

object TrySyntaxWithFailureSuite extends SuiteLike("Try Syntax failure_? Suite") {

  private val t1 = test("Try Syntax failure_? fails on Success") {
    val failOnSuccessTest = test("fails on Success") {
      failure_?(Try("5".toInt))(_.getMessage =?= "some error" | "expected failure")
    }

    singleTestFailed(
      testName      = "fails on Success",
      assertionName = "expected Failure",
      context       = Map("value" -> "Success(5)"),
      location      = 12,
      error         = "expected Failure got: Success(5)"
    )(Boon.runTest(failOnSuccessTest))
  }

  private val t2 = test("Try Syntax failure_? passes on Failure") {
    val passOnFailureTest = test("passes on Failure") {
      failure_?(Try("five".toInt))(_.getMessage =?= "For input string: \"five\"" | "expected failure")
    }

    singleTestPassed(
      testName      = "passes on Failure",
      assertionName = "expected failure",
      context       = Map("value" -> "Failure(java.lang.NumberFormatException(For input string: \"five\"))"),
      location      = 26,
    )(Boon.runTest(passOnFailureTest))
  }

  private val t3 = test("Try Syntax failure_? fails on Assertion") {
    val failOnAssertionTest = test("fails on Assertion") {
      failure_?(Try("five".toInt))(_.getMessage =?= "some error" | "expected failure")
    }

    singleTestFailed(
      testName      = "fails on Assertion",
      assertionName = "expected failure",
      context       = Map("value" -> "Failure(java.lang.NumberFormatException(For input string: \"five\"))"),
      location      = 39,
      error         = "\"For input string: \"five\"\" != \"some error\""
    )(Boon.runTest(failOnAssertionTest))
  }

  override val tests = oneOrMore(t1, t2, t3)
}