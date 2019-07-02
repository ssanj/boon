package boon
package syntax

import boon.model.internal._
import option._

object OptionSyntaxWithIsSomeSuite extends SuiteLike("Option Syntax isSome Suite") {

  private val t1 = test("Option Syntax isSome fails on None") {
    val failOnNoneTest = test("fails on None") {
      isSome(none[Int])
    }

    singleTestFailed(
      testName      = "fails on None",
      assertionName = "is Some",
      context       = noContext,
      location      = 11,
      error         = "expected Some got: None"
    )(Boon.runTest(failOnNoneTest))
  }

  private val t2 = test("Option Syntax isSome passes on Some") {
    val passOnSomeTest = test("passes on Some") {
      isSome(20.some)
    }

    singleTestPassed(
      testName      = "passes on Some",
      assertionName = "is Some",
      context       = noContext,
      location      = 25,
    )(Boon.runTest(passOnSomeTest))
  }

  override val tests = oneOrMore(t1, t2)
}