package boon
package syntax

import boon.model.internal._
import nulls._

object NullSyntaxWithIsNullSuite extends SuiteLike("Null Syntax isNull Suite") {

  private val t1 = test("Null Syntax isNull fails on not null") {
    val failOnNotNullTest = test("fails on not null") {
      isNull("hello")
    }

    singleTestFailed(
      testName      = "fails on not null",
      assertionName = "is null",
      context       = Map("value" -> "\"hello\""),
      location      = 11,
      error         = "expected null got: \"hello\""
    )(Boon.runTest(failOnNotNullTest))
  }

  private val t2 = test("Null Syntax isNull passes on null") {
    val passOnNullTest = test("passes on null") {
      isNull[String](null)
    }

    singleTestPassed(
      testName      = "passes on null",
      assertionName = "is null",
      context       = Map("value" -> "null"),
      location      = 25,
    )(Boon.runTest(passOnNullTest))
  }

  override val tests = oneOrMore(t1, t2)
}