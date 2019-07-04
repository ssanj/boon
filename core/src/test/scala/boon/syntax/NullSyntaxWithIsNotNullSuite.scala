package boon
package syntax

import boon.model.internal._
import nulls._

object NullSyntaxWithIsNotNullSuite extends SuiteLike("Null Syntax isNotNull Suite") {

  private val t1 = test("Null Syntax isNotNull fails on null") {
    val failOnNullTest = test("fails on null") {
      isNotNull[String](null)
    }

    singleTestFailed(
      testName      = "fails on null",
      assertionName = "is not null",
      context       = Map("value" -> "null"),
      location      = 11,
      error         = "expected not null got: null"
    )(Boon.runTest(failOnNullTest))
  }

  private val t2 = test("Null Syntax isNotNull passes on not null") {
    val passOnNotNullTest = test("passes on not null") {
      isNotNull[String]("hooroo")
    }

    singleTestPassed(
      testName      = "passes on not null",
      assertionName = "is not null",
      context       = Map("value" -> "\"hooroo\""),
      location      = 25,
    )(Boon.runTest(passOnNotNullTest))
  }

  override val tests = oneOrMore(t1, t2)
}