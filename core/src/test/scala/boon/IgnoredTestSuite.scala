package boon

import boon.model.TestName
import boon.model.TestIgnoredResult
import BoonAssertions.failWith
import BoonAssertions.Expected
import BoonAssertions.Got
import BoonAssertions.Desc

object IgnoredTestSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("ignored test") {

    val tx = xtest("A test that is ignored") {
      true =?= true | "truism"
    }

    Boon.runTest(tx) match {
      case TestIgnoredResult(TestName(name)) => name =?= "A test that is ignored" | "test name"
      case other => failWith(Expected("TestIgnoredResult"), Got(other), Desc("test result type"))
    }
  }

  override val tests = oneOrMore(t1)
}