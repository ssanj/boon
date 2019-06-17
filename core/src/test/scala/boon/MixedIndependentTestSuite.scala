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
import syntax.collection.positional
import internal.instances._

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
        positional(assertionResults, "results"){
          oneOrMore(
            assertAssertionResultPassed("truism"),
            assertAssertionResultFailed("falsism"),
            assertAssertionResultThrew(
              "error",
              _ =!=[NotImplementedError](_ =?= "an implementation is missing" | "assertion thrown")
            )
          )
        }

      case other => failWith(Expected("SingleTestResult"), Got(other), Desc("test result type"))
    }
  }
  override val tests = oneOrMore(t1)
}