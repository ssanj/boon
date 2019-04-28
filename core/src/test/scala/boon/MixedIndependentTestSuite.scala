package boon

import scala.NotImplementedError
import boon.model.Independent
import boon.model.TestName
import boon.model.DeferredTest
import boon.model.SingleTestResult
import syntax.exception._
import BoonAssertions.failWith
import BoonAssertions.assertAssertionResultPassed
import BoonAssertions.assertAssertionResultFailed
import BoonAssertions.assertAssertionResultThrew
import BoonAssertions.Expected
import BoonAssertions.Got
import BoonAssertions.Desc

object MixedIndependentTestSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("mixed test - independent") {

    val tx = test("success + fails + errors") {
      true =?= true  | "truism"  and
      false =?= true | "falsism" and
      true =?= ???   | "error"
    }

    Boon.runTest(tx) match {
      case SingleTestResult(DeferredTest(TestName(name), _, Independent), assertionResults) =>
        name =?= "success + fails + errors" | "test name"   and
        assertionResults.length =?= 3 | "no of assertions"  and %@(assertionResults.toSeq) { ar =>
          assertAssertionResultPassed("truism")(ar(0))      and
          assertAssertionResultFailed("falsism")(ar(1))     and
          assertAssertionResultThrew(
            "error",
            _ =!=[NotImplementedError](_ =?= "an implementation is missing" | "assertion thrown")
          )(ar(2))
        }

      case other => failWith(Expected("SingleTestResult"), Got(other), Desc("test result type"))
    }
  }
  override val tests = oneOrMore(t1)
}