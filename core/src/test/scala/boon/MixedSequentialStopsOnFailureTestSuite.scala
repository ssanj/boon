package boon

import boon.model.AssertionName
import boon.model.Assertion
import boon.model.AssertionError
import boon.model.SequentialFail
import boon.model.AssertionName
import boon.model.FirstFailed
import boon.model.TestName
import boon.model.StoppedOnFirstFailed
import boon.model.CompositeTestResult
import BoonAssertions.failWith
import BoonAssertions.assertSequentialPass
import BoonAssertions.Expected
import BoonAssertions.Got
import BoonAssertions.Desc
import BoonAssertions.seqElements1

object MixedSequentialStopsOnFailureTestSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("mixed sequential test - stops on failure") {

    val tx = test("success + fails + stops") {
      true =?= true  | "truism"  and
      false =?= true | "falsism" and
      true =?= ???   | "error"   seq()
    }

    val result = Boon.runTest(tx)

    result match {
      case CompositeTestResult(
        StoppedOnFirstFailed(TestName(testName),
          FirstFailed(
            AssertionName(failedAssertionName),
              Left(SequentialFail(AssertionError(Assertion(AssertionName(aName), _, _, _), errors))),
              passed, notRun))) =>

        testName =?= "success + fails + stops" | "test name"        and
        failedAssertionName =?= "falsism" | "failed assertion"      and
        failedAssertionName =?= aName | "assertion name"            and
        errors =?= oneOrMore("false != true") | "failure reason"    and
        seqElements1(passed, "passed")(assertSequentialPass("truism")) and
        seqElements1(notRun, "notRun")(_.name.value =?= "error" | "not run assertion name")

      case other => failWith(Expected("CompositeTestResult/StoppedOnFirstFailed/SequentialFail"), Got(other), Desc("test result type"))
    }
  }

  override val tests = oneOrMore(t1)
}