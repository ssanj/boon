package boon
package syntax

import boon.model.AssertionTriple
import boon.model.AssertionName
import boon.model.TestName
import boon.model.internal._

import option._
import collection._

object OptionSyntaxWithSomeSuite extends SuiteLike("Option Syntax some_? Suite") {

  private val t1 = test("some_? fails on None") {
    val failOnNoneTest = test("fail on None") {
      some_?(none[Int])(_ =?= 20 | "twenty")
    }

    val result = Boon.runTest(failOnNoneTest)
    getSingleTestFailed(result) match {
      case Some(SingleTestFailed(TestName(tName), AssertionName(aName),context, loc, errors)) =>
        tName =?= "fail on None" | "test name"      and
        aName =?= "expect Some"  | "assertion name" and
        context =?= noContext    | "context"        and
        loc.line =?= 16          | "location"       and
        positional(errors, "assertion.errors")(one(_ =?= "explicit fail: expected Some but got None" | "was some"))

      case None => invalid(s"Expected test failure, got: $result") | "test type"
    }
  }

  private val t2 = test("some_? passes on Some") {
    val passOnSomeTest = test("pass on Some") {
      some_?(20.some)(_ =?= 20 | "twenty")
    }

    val result = Boon.runTest(passOnSomeTest)
    getSingleTestPassed(result) match {
      case Some(SingleTestPassed(TestName(tName), AssertionTriple(AssertionName(aName), context, loc))) =>
        tName =?= "pass on Some"               | "test name"      and
        aName =?= "twenty"                     | "assertion name" and
        context =?= Map("value" -> "Some(20)") | "context"        and
        loc.line =?= 34                        | "location"

      case None => invalid(s"Expected test success, got: $result") | "test type"
    }
  }

  private val t3 = test("some_? fails on Assertion error") {
    val passOnSomeTest = test("fail on Some Assertion") {
      some_?(50.some)(_ =?= 20 | "twenty")
    }

    val result = Boon.runTest(passOnSomeTest)
    getSingleTestFailed(result) match {
      case Some(SingleTestFailed(TestName(tName), AssertionName(aName),context, loc, errors)) =>
        tName =?= "fail on Some Assertion"     | "test name"      and
        aName =?= "twenty"                     | "assertion name" and
        context =?= Map("value" -> "Some(50)") | "context"        and
        loc.line =?= 51                        | "location"       and
        positional(errors, "assertion.errors")(one(_ =?= "50 != 20" | "was twenty"))

      case None => invalid(s"Expected test failure, got: $result") | "test type"
    }
  }

  override val tests = oneOrMore(t1, t2, t3)
}