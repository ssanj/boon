package boon

import model.TestThrewResult
import model.ThrownTest
import model.TestName
import model.TestData
import model.TestIgnoredResult
import syntax._

object BoonSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("UnsuccessfulTest") {

    def createTestData(): TestData = { throw new RuntimeException("some exception") }

    val tx = test("A test that throws") {
      createTestData()
    }

    val result = Boon.runTest(tx)
    result match {
      case TestThrewResult(ThrownTest(TestName(name), error, loc)) =>
        name =?= "A test that throws"         | "test name" and
        error.getMessage =?= "some exception" | "error message" and
        loc.line =?= 16                       | "error location"

      case other => fail(s"Expected TestThrewResult but got $other") | "invalid test result"
    }
  }

  private val t2 = test("ignoredTest") {

    val tx = xtest("A test that is ignored") {
      true =?= true | "truism"
    }

    val result = Boon.runTest(tx)
    result match {
      case TestIgnoredResult(TestName(name)) => name =?= "A test that is ignored" | "test name"
      case other => fail(s"Expected TestIgnoredResult but got $other") | "invalid test result"
    }
  }

  override val tests = oneOrMore(t1, t2)
}