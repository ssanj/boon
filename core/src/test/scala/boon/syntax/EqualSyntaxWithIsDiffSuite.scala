package boon
package syntax

import boon.model.internal._
import equal._

object EqualSyntaxWithIsDiffSuite extends SuiteLike("Equal Syntax isDiff Suite") {

  private val t1 = test("Equal Syntax isDiff fails on equals") {
    val failOnEquals = test("fails on equals") {
      val expectation = isDiff(10)
      expectation(10)
    }

    singleTestFailed(
      testName      = "fails on equals",
      assertionName = "10 is different to 10",
      context       = Map("value" -> "10"),
      location      = 11,
      error         = "10 != 10"
    )(Boon.runTest(failOnEquals))
  }

  private val t2 = test("Equal Syntax isDiff passes on differences") {
    val passOnDifferences = test("passes on differences") {
      val expectation = isDiff(10)
      expectation(12)
    }

    singleTestPassed(
      testName      = "passes on differences",
      assertionName = "10 is different to 12",
      context       = Map("value" -> "12"),
      location      = 26
    )(Boon.runTest(passOnDifferences))
  }

  override val tests = oneOrMore(t1, t2)
}