package boon
package syntax

import boon.model.internal._
import nulls._

object NullSyntaxWithNotNullSuite extends SuiteLike("Null Syntax null_! Suite") {

  private val t1 = test("Null Syntax null_! fails on not null") {
    val failOnNullTest = test("fails on null") {
      null_![String](null)(_ =?= "something" | "is not null thingo")
    }

    singleTestFailed(
      testName      = "fails on null",
      assertionName = "expected not null",
      context       = Map("value" -> "null"),
      location      = 11,
      error         = "expected not null value"
    )(Boon.runTest(failOnNullTest))
  }

  private val t2 = test("Null Syntax null_! passes on not null") {
    val passOnNotNullTest = test("passes on not null") {
      null_![String]("hooroo")(_ =?= "hooroo" | "is not null thingo")
    }

    singleTestPassed(
      testName      = "passes on not null",
      assertionName = "is not null thingo",
      context       = Map("value" -> "\"hooroo\""),
      location      = 25,
    )(Boon.runTest(passOnNotNullTest))
  }

  override val tests = oneOrMore(t1, t2)
}