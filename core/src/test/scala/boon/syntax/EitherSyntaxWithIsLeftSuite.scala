package boon
package syntax

import boon.model.internal._
import either._

object EitherSyntaxWithIsLeftSuite extends SuiteLike("Either Syntax isLeft Suite") {

  private val t1 = test("Either Syntax isLeft fails on Right") {
    val failOnRightTest = test("fails on Right") {
      isLeft(100.right[String])
    }

    singleTestFailed(
      testName      = "fails on Right",
      assertionName = "is Left",
      context       = Map("value" -> "Right(100)"),
      location      = 11,
      error         = "expected Left got: Right(100)"
    )(Boon.runTest(failOnRightTest))
  }

  private val t2 = test("Either Syntax isLeft passes on Left") {
    val passOnLeftTest = test("passes on Left") {
      isLeft("some value".left[Int])
    }

    singleTestPassed(
      testName      = "passes on Left",
      assertionName = "is Left",
      context       = Map("value" -> "Left(\"some value\")"),
      location      = 25,
    )(Boon.runTest(passOnLeftTest))
  }

  override val tests = oneOrMore(t1, t2)
}