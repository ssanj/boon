package boon
package syntax

import boon.model.internal._
import option._

object OptionSyntaxWithNoneSuite extends SuiteLike("Option Syntax none_? Suite") {

  private val t1 = test("Option Syntax none_? fails on Some") {
    val failOnNoneTest = test("fails on Some") {
      none_?(20.some)(pass | "is none")
    }

    singleTestFailed(
      testName      = "fails on Some",
      assertionName = "expect None",
      context       = Map("value" -> "Some(20)"),
      location      = 11,
      error         = "explicit fail: expected None but got: Some(20)" 
    )(Boon.runTest(failOnNoneTest))
  }

  private val t2 = test("Option Syntax none_? passes on None") {
    val passOnSomeTest = test("passes on None") {
      none_?(none[Int])(pass | "is none")
    }

    singleTestPassed(
      testName      = "passes on None",
      assertionName = "is none",
      context       = noContext,
      location      = 25,
    )(Boon.runTest(passOnSomeTest))
  }

  private val t3 = test("Option Syntax none_? fails on Assertion") {
    val failOnAssertion = test("fails on Assertion") {
      none_?(none[Int])(fail("some error") | "is none")
    }

    singleTestFailed(
      testName      = "fails on Assertion",
      assertionName = "is none",
      context       = noContext,
      location      = 38,
      error         = "explicit fail: some error" 
    )(Boon.runTest(failOnAssertion))    
  }

  override val tests = oneOrMore(t1, t2, t3)
}