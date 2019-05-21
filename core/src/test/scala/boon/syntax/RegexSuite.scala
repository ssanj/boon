package boon
package syntax

import boon.model.TestState.Failed
import boon.model.TestState.Passed
import boon.model.TestResult
import boon.model.internal.instances._
import regex._

object RegexSuite extends SuiteLike("RegexSuite") {

  private val missTest = test("regex miss") {
    "Hello" =^= "^.*HH.*$".r | "miss"
  }

  private val matchTest = test("regex match") {
    "Hello" =^= "^.*ll.*$".r | "passes on a valid match"
  }

  private val t1 = test("test result") {
    %@(Boon.runTest(matchTest)) { tr =>
      TestResult.testResultToTestState(tr) =?= Passed | "passes on valid regex"
    } and
    %@(Boon.runTest(missTest)) { tr =>
      TestResult.testResultToTestState(tr) =?= Failed | "fails on invalid regex"
    }
  }

  override val tests = oneOrMore(t1, matchTest)
}