package boon
package syntax

import boon.model.internal._
import option._

object OptionSyntaxWithIsNoneSuite extends SuiteLike("Option Syntax isNone Suite") {

  private val t1 = test("Option Syntax isNone fails on Some") {
    val failOnSomeTest = test("fails on Some") {
      isNone(20.some)
    }

    singleTestFailed(
      testName      = "fails on Some",
      assertionName = "is None",
      context       = Map("value" -> "Some(20)"),
      location      = 11,
      error         = "expected None got: Some(20)"
    )(Boon.runTest(failOnSomeTest))
  }

  private val t2 = test("Option Syntax isNone passes on None") {
    val passOnNoneTest = test("passes on None") {
      isNone(none[String])
    }

    singleTestPassed(
      testName      = "passes on None",
      assertionName = "is None",
      context       = Map("value" -> "None"),
      location      = 25,
    )(Boon.runTest(passOnNoneTest))
  }

  override val tests = oneOrMore(t1, t2)
}