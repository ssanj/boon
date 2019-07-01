package boon
package syntax

import boon.model.AssertionTriple
import boon.model.AssertionName
import boon.model.TestName
import boon.model.internal._

import option._
import collection._

object OptionSyntaxWithNoneSuite extends SuiteLike("Option Syntax none_? Suite") {

  private val t1 = test("none_? fails on Some") {
    val failOnNoneTest = test("fail on Some") {
      none_?(20.some)(pass | "is none")
    }

    val result = Boon.runTest(failOnNoneTest)
    getSingleTestFailed(result) match {
      case Some(SingleTestFailed(TestName(tName), AssertionName(aName),context, loc, errors)) =>
        tName =?= "fail on Some"               | "test name"      and
        aName =?= "expect None"                | "assertion name" and
        context =?= Map("value" -> "Some(20)") | "context"        and
        loc.line =?= 16                        | "location"       and
        positional(errors, "assertion.errors")(one(_ =?= "explicit fail: expected None but got: Some(20)" | "was none"))

      case None => invalid(s"Expected test failure, got: $result") | "test type"
    }
  }

  private val t2 = test("none_? passes on None") {
    val passOnSomeTest = test("pass on None") {
      none_?(none[Int])(pass | "is none")
    }

    val result = Boon.runTest(passOnSomeTest)
    getSingleTestPassed(result) match {
      case Some(SingleTestPassed(TestName(tName), AssertionTriple(AssertionName(aName), context, loc))) =>
        tName =?= "pass on None" | "test name"      and
        aName =?= "is none"      | "assertion name" and
        context =?= noContext    | "context"        and
        loc.line =?= 34          | "location"

      case None => invalid(s"Expected test success, got: $result") | "test type"
    }
  }

  private val t3 = test("none_? fails on Assertion error") {
    val passOnSomeTest = test("fail on None Assertion") {
      none_?(none[Int])(fail("some error") | "is none")
    }

    val result = Boon.runTest(passOnSomeTest)
    getSingleTestFailed(result) match {
      case Some(SingleTestFailed(TestName(tName), AssertionName(aName),context, loc, errors)) =>
        tName =?= "fail on None Assertion" | "test name"      and
        aName =?= "is none"                | "assertion name" and
        context =?= noContext              | "context"        and
        loc.line =?= 51                    | "location"       and
        positional(errors, "assertion.errors")(one(_ =?= "explicit fail: some error" | "fail assertion"))

      case None => invalid(s"Expected test failure, got: $result") | "test type"
    }
  }

  override val tests = oneOrMore(t1, t2, t3)
}