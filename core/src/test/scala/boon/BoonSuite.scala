package boon

import model._
import syntax._
import exception._

object BoonSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("unsuccessfulTest") {

    def createTestData(): TestData = { throw new RuntimeException("some exception") }

    val tx = test("A test that throws") {
      createTestData()
    }

    val result = Boon.runTest(tx)
    result match {
      case TestThrewResult(ThrownTest(TestName(name), error, loc)) =>
        name =?= "A test that throws"         | "test name" and
        error =!=[RuntimeException](_ =?= "some exception" | "error message") and
        loc.line =?= 13                       | "error location"

      case other => fail(s"Expected TestThrewResult but got $other") | "test result type"
    }
  }

  private val t2 = test("ignoredTest") {

    val tx = xtest("A test that is ignored") {
      true =?= true | "truism"
    }

    val result = Boon.runTest(tx)
    result match {
      case TestIgnoredResult(TestName(name)) => name =?= "A test that is ignored" | "test name"
      case other => fail(s"Expected TestIgnoredResult but got $other") | "test result type"
    }
  }

  private val t3 = test("successfulTest") {
    val tx = test("String test") {
      "Hello" + " " + "World" =?= "Hello World" | "concat" and
      "Hello".length =?= 5                      | "length" and
      "World".reverse =?= "dlroW"               | "reverse"
    }

    val result = Boon.runTest(tx)
    result match {
      case SingleTestResult(DeferredTest(TestName(name), _, _), assertionResults: NonEmptySeq[AssertionResult]) =>
        name =?= "String test" | "test name" and
        assertionResults.length =?= 3 | "no of assertions" and %@(assertionResults.toSeq) { ar =>
          %@(ar(0), "assertion1")(assertAssertionResultPassed("concat")) and
          %@(ar(1), "assertion2")(assertAssertionResultPassed("length")) and
          %@(ar(2), "assertion3")(assertAssertionResultPassed("reverse"))
        }

      case other => fail(s"Expected SingleTestResult but got $other") | "test result type"
    }
  }

  private def assertAssertionResultPassed(assertionName: String)(ar: AssertionResult): AssertionData = {
    ar match {
      case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(aName), context, _))) =>
        aName =?= assertionName | "assertion name"
      case other => fail(s"Expected SingleAssertionResult/AssertionResultPassed got $other") | "assert result type"
    }
  }

  override val tests = oneOrMore(t1, t2, t3)
}