package boon

import boon.model.TestData
import boon.model.TestName
import boon.model.ThrownTest
import boon.model.TestThrewResult
import boon.result.Exception.getTraces

import syntax.exception._
import syntax.regex._
import BoonAssertions.failWith
import BoonAssertions.Expected
import BoonAssertions.Got
import BoonAssertions.Desc

object UnsuccessfulTestSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("unsuccessful test") {

    def createTestData(): TestData = { throw new RuntimeException("some exception") }

    val tx = test("A test that throws") {
      createTestData()
    }

    Boon.runTest(tx) match {
      case TestThrewResult(ThrownTest(TestName(name), error, loc)) =>
        name =?= "A test that throws"                            | "test name"       and
        error =!=[RuntimeException](_ =?= "some exception"       | "error message")  and
        %@(getTraces(error, 1)(0), "stacktrace") { trace =>
          trace.className =^= "^boon.UnsuccessfulTestSuite".r    | "class name"      and
          trace.fileName =?= Some("UnsuccessfulTestSuite.scala") | "fileName"        and
          trace.methodName =^= "^createTestData".r               | "method name"     and
          trace.lineNumber =?= Some(20)                          | "line no"
        }                                                                            and
        loc.line =?= 22                                          | "error location"

      case other => failWith(Expected("TestThrewResult"), Got(other), Desc("test result type"))
    }
  }

  override val tests = oneOrMore(t1)
}