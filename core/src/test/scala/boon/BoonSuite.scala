package boon

import model._
import syntax._
import exception._

object BoonSuite extends SuiteLike("BoonSuite") {

  private def failWith[A](expected: String, other: => A, assertionName: String): AssertionData =
    fail(s"Expected $expected but got $other") | assertionName

  private val t1 = test("unsuccessfulTest") {

    def createTestData(): TestData = { throw new RuntimeException("some exception") }

    val tx = test("A test that throws") {
      createTestData()
    }

    val result = Boon.runTest(tx)
    result match {
      case TestThrewResult(ThrownTest(TestName(name), error, loc)) =>
        name =?= "A test that throws"         | "test name" and
        error =!=[RuntimeException](_ =?= "some exception" | "error message") and
        loc.line =?= 16                       | "error location"

      case other => failWith("TestThrewResult", other, "test result type")
    }
  }

  private val t2 = test("ignoredTest") {

    val tx = xtest("A test that is ignored") {
      true =?= true | "truism"
    }

    val result = Boon.runTest(tx)
    result match {
      case TestIgnoredResult(TestName(name)) => name =?= "A test that is ignored" | "test name"
      case other => failWith("TestIgnoredResult", other, "test result type")
    }
  }

  private val t3 = test("successfulTest.independent") {
    val tx = test("String test") {
      "Hello" + " " + "World" =?= "Hello World" | "concat" and
      "Hello".length =?= 5                      | "length" and
      "World".reverse =?= "dlroW"               | "reverse"
    }

    def assertAssertionResultPassed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(aName), context, _))) =>
          aName =?= assertionName | "assertion name"
        case other => failWith("SingleAssertionResult/AssertionResultPassed", other, "assert result type")
      }
    }

    val result = Boon.runTest(tx)
    result match {
      case SingleTestResult(DeferredTest(TestName(name), _, Independent), assertionResults: NonEmptySeq[AssertionResult]) =>
        name =?= "String test" | "test name" and
        assertionResults.length =?= 3 | "no of assertions" and %@(assertionResults.toSeq) { ar =>
          %@(ar(0), "assertion1")(assertAssertionResultPassed("concat")) and
          %@(ar(1), "assertion2")(assertAssertionResultPassed("length")) and
          %@(ar(2), "assertion3")(assertAssertionResultPassed("reverse"))
        } seq()

      case other => failWith(s"SingleTestResult", other, "test result type")
    }
  }

  private val t4 = test("successfulTest.sequential") {
    val tx = test("NonEmptySeq test") {
      val saturdayMenu: NonEmptySeq[String] =
        oneOrMore(
          "Chocolate cake",
          "Ice cream cone",
          "Pickle",
          "Swiss cheese",
          "Slice of Salami",
          "Lollipop",
          "Cherry Pie",
          "Sausage",
          "Cup cake",
          "Slice of watermelon"
        )

      saturdayMenu.length =?= 10                                                            | "length" and
      saturdayMenu.head   =?= "Chocolate cake"                                              | "head"   and
      saturdayMenu.last   =?= "Slice of watermelon"                                         | "last"   and
      saturdayMenu.contains("Pickle") >> one(s"Could not find 'Pickle' in $saturdayMenu")   | "contains" seq()
    }

    def assertSequentialPass(assertionName: String)(sp: SequentialPass): AssertionData = {
      sp.name.value =?= assertionName | "assertion name"
    }

    val result = Boon.runTest(tx)
    result match {
      case CompositeTestResult(AllPassed(TestName(name), passed)) =>
        name =?= "NonEmptySeq test" | "test name" and
        passed.length =?= 4   | "no of assertions"  and %@(passed.toSeq) { p =>
          %@(p(0), "assertion1"){ assertSequentialPass("length")   } and
          %@(p(1), "assertion2"){ assertSequentialPass("head")     } and
          %@(p(2), "assertion3"){ assertSequentialPass("last")     } and
          %@(p(3), "assertion4"){ assertSequentialPass("contains") }
        }

      case other => failWith("CompositeTestResult", other, "test result type")
    }
  }

  override val tests = oneOrMore(t1, t2, t3, t4)
}