package boon

import boon.model.Independent
import boon.model.TestName
import boon.model.DeferredTest
import boon.model.SingleTestResult
import BoonAssertions.failWith
import BoonAssertions.assertAssertionResultPassed

object SuccessfulIndependentTestSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("successful test - independent") {
    val tx = test("String test") {
      "Hello" + " " + "World" =?= "Hello World" | "concat" and
      "Hello".length =?= 5                      | "length" and
      "World".reverse =?= "dlroW"               | "reverse"
    }

    Boon.runTest(tx) match {
      case SingleTestResult(DeferredTest(TestName(name), _, Independent), assertionResults) =>
        name =?= "String test" | "test name"               and
        assertionResults.length =?= 3 | "no of assertions" and %@(assertionResults.toSeq) { ar =>
          (assertAssertionResultPassed("concat")(ar(0)))   and
          (assertAssertionResultPassed("length")(ar(1)))   and
          (assertAssertionResultPassed("reverse")(ar(2)))
        } seq()

      case other => failWith(s"SingleTestResult", other, "test result type")
    }
  }

  override val tests = oneOrMore(t1)
}