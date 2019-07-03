package boon
package syntax

import boon.model.internal._
import nulls._

object NullSyntaxWithNullSuite extends SuiteLike("Null Syntax null_? Suite") {

  private val t1 = test("Null Syntax null_? fails on not null") {
    val failOnNotNullTest = test("fails on not null") {
      null_?("hello")(pass | "is null thingo")
    }

    singleTestFailed(
      testName      = "fails on not null",
      assertionName = "expected null",
      context       = Map("value" -> "\"hello\""),
      location      = 11,
      error         = "expected null got: \"hello\""
    )(Boon.runTest(failOnNotNullTest))
  }

  private val t2 = test("Null Syntax null_? passes on null") {
    val passOnNullTest = test("passes on null") {
      null_?[String](null)(pass | "is null thingo")
    }

    singleTestPassed(
      testName      = "passes on null",
      assertionName = "is null thingo",
      context       = Map("value" -> "null"),
      location      = 25,
    )(Boon.runTest(passOnNullTest))
  }

  override val tests = oneOrMore(t1, t2)
}