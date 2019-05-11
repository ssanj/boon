package boon

import boon.model.Independent
import boon.model.TestName
import boon.model.DeferredTest
import boon.model.SingleTestResult
import BoonAssertions.failWith
import BoonAssertions.assertAssertionResultPassed
import BoonAssertions.Expected
import BoonAssertions.Got
import BoonAssertions.Desc
import BoonAssertions.nesElements3

object SuccessfulIndependentTestSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("successful test - independent") {
    val tx = test("String test") {
      "Hello" + " " + "World" =?= "Hello World" | "concat" and
      "Hello".length =?= 5                      | "length" and
      "World".reverse =?= "dlroW"               | "reverse"
    }

    Boon.runTest(tx) match {
      case SingleTestResult(DeferredTest(TestName(name), _, Independent), assertionResults) =>
        name =?= "String test" | "test name" and
        nesElements3(assertionResults, "results")(
          assertAssertionResultPassed("concat"),
          assertAssertionResultPassed("length"),
          assertAssertionResultPassed("reverse")
        ) seq()

      case other => failWith(Expected(s"SingleTestResult"), Got(other), Desc("test result type"))
    }
  }

  override val tests = oneOrMore(t1)
}