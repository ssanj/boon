package boon
package syntax

import boon.model.internal._
import either._

object EitherSyntaxWithLeftSuite extends SuiteLike("Either Syntax left_? Suite") {

  private val t1 = test("Either Syntax left_? fails on Right") {
    val failOnRightTest = test("fails on Right") {
      left_?(20.right[String])(_ =?= "some error" | "some error")
    }

    singleTestFailed(
      testName      = "fails on Right",
      assertionName = "expected Left",
      context       = Map("value" -> "Right(20)"),
      location      = 11,
      error         = "expected Left got: Right(20)"
    )(Boon.runTest(failOnRightTest))
  }

  private val t2 = test("Either Syntax left_? passes on Left") {
    val passOnLeftTest = test("passes on Left") {
      left_?("some error".left[Int])(_ =?= "some error" | "some error")
    }

    singleTestPassed(
      testName      = "passes on Left",
      assertionName = "some error",
      context       = Map("value" -> "Left(\"some error\")"),
      location      = 25,
    )(Boon.runTest(passOnLeftTest))
  }

  private val t3 = test("Either Syntax left_? fails on Assertion") {
    val failOnLeftTest = test("fails on Assertion") {
      left_?("some error".left[Int])(_ =?= "one error" | "some error")
    }

    singleTestFailed(
      testName      = "fails on Assertion",
      assertionName = "some error",
      context       = Map("value" -> "Left(\"some error\")"),
      location      = 38,
      error         = "\"some error\" != \"one error\""
    )(Boon.runTest(failOnLeftTest))
  }

  override val tests = oneOrMore(t1, t2, t3)
}