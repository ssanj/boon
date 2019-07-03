package boon
package syntax

import boon.model.internal._
import either._

object EitherSyntaxWithIsRightSuite extends SuiteLike("Either Syntax isRight Suite") {

  private val t1 = test("Either Syntax isRight fails on Left") {
    val failOnLeftTest = test("fails on Left") {
      isRight("some error".left[Int])
    }

    singleTestFailed(
      testName      = "fails on Left",
      assertionName = "is Right",
      context       = Map("value" -> "Left(\"some error\")"),
      location      = 11,
      error         = "expected Right got: Left(\"some error\")"
    )(Boon.runTest(failOnLeftTest))
  }

  private val t2 = test("Either Syntax isRight passes on Right") {
    val passOnRightTest = test("passes on Right") {
      isRight(20.right[String])
    }

    singleTestPassed(
      testName      = "passes on Right",
      assertionName = "is Right",
      context       = Map("value" -> "Right(20)"),
      location      = 25,
    )(Boon.runTest(passOnRightTest))
  }

  override val tests = oneOrMore(t1, t2)
}