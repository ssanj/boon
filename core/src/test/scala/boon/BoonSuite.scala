package boon

import boon.model.TestName
import boon.model.Test
import boon.model.Independent
import boon.model.SingleTestResult
import boon.model.DeferredTest
import boon.model.AssertionData
import boon.model.TestResult
import boon.model.DeferredSuite
import boon.data.NonEmptySeq
import boon.model.SuiteName
import boon.model.SuiteResult
import BoonAssertions.assertAssertionResultPassed
import BoonAssertions.failWith

object BoonSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("successful suite") {

    val tx1 = test("boolean test") {
      true =?= true | "truism"
    }

    val tx2 = test("int test") {
      2 + 2 =?= 4 | "addition"
    }

    val sx = createSuite("My very own suite")(oneOrMore(tx1, tx2))

    Boon.runSuite(sx) match {
      case SuiteResult(DeferredSuite(SuiteName(name), NonEmptySeq(tx1, tx2)), testResults) =>
        name =?= "My very own suite" | "suite name"         and
        testResults.length =?= 2     | "no of test results" and %@(testResults.toSeq) { tr =>
          %@(tr(0), "test1")(assertSingleTestResult("boolean test", "truism"))  and
          %@(tr(1), "test2")(assertSingleTestResult("int test", "addition"))
        }

       case other => failWith("SuiteResult with 2 tests", other, "suite type")
    }
  }

  private def assertSingleTestResult(testName: String, assertionName: String)(tr: TestResult): AssertionData = tr match {
    case SingleTestResult(DeferredTest(TestName(tName), _, Independent), assertionResults) =>
      tName =?= testName             | "test name"           and
      assertionResults.length =?= 1  | "no of assertions"    and
      assertAssertionResultPassed(assertionName)(assertionResults.toSeq(0))
    case other => failWith("SingleTestResult", other, "test type")
  }

  private def createSuite(name: => String)(tests: NonEmptySeq[Test]): DeferredSuite =
    DeferredSuite(SuiteName(name), tests)

  override val tests = oneOrMore(t1)
}