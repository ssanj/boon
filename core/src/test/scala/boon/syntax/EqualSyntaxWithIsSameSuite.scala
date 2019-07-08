package boon
package syntax

import boon.model.internal._
import equal._

object EqualSyntaxWithIsSameSuite extends SuiteLike("Equal Syntax isSame Suite") {

  private val t1 = test("Equal Syntax isSame fails on differences") {
    val failOnDifferences = test("fails on differences") {
      val expectation = isSame(10)
      expectation(12)
    }

    singleTestFailed(
      testName      = "fails on differences",
      assertionName = "10 is same as 12",
      context       = Map("value" -> "12"),
      location      = 11,
      error         = "10 != 12"
    )(Boon.runTest(failOnDifferences))
  }

  private val t2 = test("Equal Syntax isSame passes on equals") {
    val passOnEquals = test("passes on equals") {
      val expectation = isSame(10)
      expectation(10)
    }

    singleTestPassed(
      testName      = "passes on equals",
      assertionName = "10 is same as 10",
      context       = Map("value" -> "10"),
      location      = 26
    )(Boon.runTest(passOnEquals))
  }

  override val tests = oneOrMore(t1, t2)
}