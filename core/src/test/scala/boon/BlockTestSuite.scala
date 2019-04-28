package boon

import boon.model.Assertion
import boon.BoonAssertions.Desc
import boon.BoonAssertions.Got
import boon.BoonAssertions.Expected
import boon.BoonAssertions.failWith
import boon.model.AssertionResult
import boon.model.SingleAssertionResult
import boon.model.AssertionResultPassed
import boon.model.Independent
import boon.model.TestName
import boon.model.DeferredTest
import boon.model.SingleTestResult

object BlockSuite extends SuiteLike("Block Test Suite") {

  private val t1 = test("multiple assertions") {
    val tx = test("my block test") {
      %@(List(1,2,3)) { l =>
        l.length =?= 3              | "length" and
        l.reverse =?= List(3, 2, 1) | "reverse"
      }
    }

    Boon.runTest(tx) match {
      case SingleTestResult(DeferredTest(TestName(name), assertions, Independent), assertionResults) =>
        name =?= "my block test" | "test name" and
        nesElements[Assertion](assertions, 2, as =>
          as(0).name.value =?= "length"  | "assertion" and
          as(1).name.value =?= "reverse" | "assertion"
        ) and assertionResults.toSeq.forall {
          case SingleAssertionResult(AssertionResultPassed(_)) =>  true
          case _ => false
        } | ("all passed",
             "results" -> assertionResults.map(ar => AssertionResult.assertionNameFromResult(ar).value).mkString(","))

      case other => failWith(Expected("SingleTestResult"),  Got(other), Desc("test type"))
    }
  }

  override val tests = oneOrMore(t1)
}