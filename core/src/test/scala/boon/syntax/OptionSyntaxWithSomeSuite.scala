package boon
package syntax

import boon.model.internal._
import option._

object OptionSyntaxWithSomeSuite extends SuiteLike("Option Syntax some_? Suite") {

  private val t1 = test("Option Syntax some_? fails on None") {
    val failOnNoneTest = test("fails on None") {
      some_?(none[Int])(_ =?= 20 | "twenty")
    }

    singleTestFailed(
      testName      = "fails on None",
      assertionName = "expect Some",
      context       = noContext,
      location      = 11,
      error         = "expected Some but got None"
    )(Boon.runTest(failOnNoneTest))
  }

  private val t2 = test("Option Syntax some_? passes on Some") {
    val passOnSomeTest = test("passes on Some") {
      some_?(20.some)(_ =?= 20 | "twenty")
    }

    singleTestPassed(
      testName      = "passes on Some",
      assertionName = "twenty",
      context       = Map("value" -> "Some(20)"),
      location      = 25,
    )(Boon.runTest(passOnSomeTest))
  }

  private val t3 = test("Option Syntax some_? fails on Assertion") {
    val passOnSomeTest = test("fails on Assertion") {
      some_?(50.some)(_ =?= 20 | "twenty")
    }

    singleTestFailed(
      testName      = "fails on Assertion",
      assertionName = "twenty",
      context       = Map("value" -> "Some(50)"),
      location      = 38,
      error         = "50 != 20"
    )(Boon.runTest(passOnSomeTest))
  }

  override val tests = oneOrMore(t1, t2, t3)
}