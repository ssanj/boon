package boon
package syntax

import boon.model.internal._
import either._

object EitherSyntaxWithRightSuite extends SuiteLike("Either Syntax right_? Suite") {

  private val t1 = test("Either Syntax right_? fails on Left") {
    val failOnLeftTest = test("fails on Left") {
      right_?("someError".left[Int])(_ =?= 20 | "twenty")
    }

    singleTestFailed(
      testName      = "fails on Left",
      assertionName = "expected Right",
      context       = Map("value" -> "Left(\"someError\")"),
      location      = 11,
      error         = "expected Right got: Left(\"someError\")"
    )(Boon.runTest(failOnLeftTest))
  }

  private val t2 = test("Either Syntax right_? passes on Right") {
    val passOnRightTest = test("passes on Right") {
      right_?(20.right[String])(_ =?= 20 | "twenty")
    }

    singleTestPassed(
      testName      = "passes on Right",
      assertionName = "twenty",
      context       = Map("value" -> "Right(20)"),
      location      = 25,
    )(Boon.runTest(passOnRightTest))
  }

  private val t3 = test("Either Syntax right_? fails on Assertion") {
    val failOnRightTest = test("fails on Assertion") {
      right_?(50.right[String])(_ =?= 20 | "twenty")
    }

    singleTestFailed(
      testName      = "fails on Assertion",
      assertionName = "twenty",
      context       = Map("value" -> "Right(50)"),
      location      = 38,
      error         = "50 != 20"
    )(Boon.runTest(failOnRightTest))
  }

  override val tests = oneOrMore(t1, t2, t3)
}