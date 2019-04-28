package boon

import scala.NotImplementedError
import boon.model.AssertionName
import boon.model.AssertionThrow
import boon.model.SequentialThrew
import boon.model.AssertionName
import boon.model.FirstFailed
import boon.model.TestName
import boon.model.StoppedOnFirstFailed
import boon.model.CompositeTestResult
import BoonAssertions.assertSequentialPass
import BoonAssertions.failWith
import BoonAssertions.Expected
import BoonAssertions.Got
import BoonAssertions.Desc
import syntax.exception._

object MixedSequentialStopsOnErrorTestSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("mixed sequential test - stops on error") {
    val tx = test("success + error + stops") {
      true =?= true  | "truism"  and
      true =?= ???   | "error"   and
      false =?= true | "falsism" seq()
    }

    Boon.runTest(tx) match {
      case CompositeTestResult(
        StoppedOnFirstFailed(TestName(testName),
          FirstFailed(
            AssertionName(failedAssertionName),
              Right(SequentialThrew(AssertionThrow(AssertionName(aName), error, _))),
              passed, notRun))) =>

        testName =?= "success + error + stops"    | "test name"                                and
        failedAssertionName =?= "error"           | "error assertion"                          and
        failedAssertionName =?= aName             | "failure assertion"                        and
        error =!=[NotImplementedError](_ =?= "an implementation is missing" | "error message") and
        passed.length =?= 1                       | "no of passed assertions"                  and
        assertSequentialPass("truism")(passed(0))                                              and
        notRun.length =?= 1                       | "no of notRun assertions"                  and
        notRun(0).name.value =?= "falsism"        | "not run assertion name"

      case other => failWith(Expected("CompositeTestResult/StoppedOnFirstFailed/SequentialThrew"), Got(other), Desc("test result type"))
    }
  }

  override val tests = oneOrMore(t1)
}