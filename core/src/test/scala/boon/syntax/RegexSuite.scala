package boon
package syntax

import boon.model.TestState.Failed
import boon.model.TestState.Passed
import boon.model.TestResult
import boon.internal.instances._
import regex._
import equal._

object RegexSuite extends SuiteLike("Regex Syntax Suite") {

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

  private val t2 = test("matching groups") {
    val date = raw"(\d{4})-(\d{2})-(\d{2})".r
    "2018-07-10" =^= withRegexGroups(date,  isSame("2018"), isSame("07"), isSame("10")) | "date match"
  }

  override val tests = oneOrMore(t1, t2)
}