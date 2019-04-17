package boon

import model._
import syntax._
import exception._

object BoonSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("unsuccessful test") {

    def createTestData(): TestData = { throw new RuntimeException("some exception") }

    val tx = test("A test that throws") {
      createTestData()
    }

    val result = Boon.runTest(tx)
    result match {
      case TestThrewResult(ThrownTest(TestName(name), error, loc)) =>
        name =?= "A test that throws"                      | "test name"      and
        error =!=[RuntimeException](_ =?= "some exception" | "error message") and
        loc.line =?= 13                                    | "error location"

      case other => failWith("TestThrewResult", other, "test result type")
    }
  }

  private val t2 = test("ignored test") {

    val tx = xtest("A test that is ignored") {
      true =?= true | "truism"
    }

    val result = Boon.runTest(tx)
    result match {
      case TestIgnoredResult(TestName(name)) => name =?= "A test that is ignored" | "test name"
      case other => failWith("TestIgnoredResult", other, "test result type")
    }
  }

  private val t3 = test("successful test - independent") {
    val tx = test("String test") {
      "Hello" + " " + "World" =?= "Hello World" | "concat" and
      "Hello".length =?= 5                      | "length" and
      "World".reverse =?= "dlroW"               | "reverse"
    }

    val result = Boon.runTest(tx)
    result match {
      case SingleTestResult(DeferredTest(TestName(name), _, Independent), assertionResults) =>
        name =?= "String test" | "test name"               and
        assertionResults.length =?= 3 | "no of assertions" and %@(assertionResults.toSeq) { ar =>
          (assertAssertionResultPassed("concat")(ar(0)))   and
          (assertAssertionResultPassed("length")(ar(1)))   and
          (assertAssertionResultPassed("reverse")(ar(2)))
        } seq()

      case other => failWith(s"SingleTestResult", other, "test result type")
    }
  }

  private val t4 = test("successful test - sequential") {
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

      saturdayMenu.length =?= 10                                                            | "length"   and
      saturdayMenu.head   =?= "Chocolate cake"                                              | "head"     and
      saturdayMenu.last   =?= "Slice of watermelon"                                         | "last"     and
      saturdayMenu.contains("Pickle") >> one(s"Could not find 'Pickle' in $saturdayMenu")   | "contains" seq()
    }

    val result = Boon.runTest(tx)
    result match {
      case CompositeTestResult(AllPassed(TestName(name), passed)) =>
        name =?= "NonEmptySeq test" | "test name"   and
        passed.length =?= 4   | "no of assertions"  and %@(passed.toSeq) { p =>
          assertSequentialPass("length")(p(0))      and
          assertSequentialPass("head")(p(1))        and
          assertSequentialPass("last")(p(2))        and
          assertSequentialPass("contains")(p(3))
        }

      case other => failWith("CompositeTestResult", other, "test result type")
    }
  }

  val t5 = test("mixed test - independent") {

    val tx = test("success + fails + errors") {
      true =?= true  | "truism"  and
      false =?= true | "falsism" and
      true =?= ???   | "error"
    }

    val result = Boon.runTest(tx)

    result match {
      case SingleTestResult(DeferredTest(TestName(name), _, Independent), assertionResults) =>
        name =?= "success + fails + errors" | "test name"   and
        assertionResults.length =?= 3 | "no of assertions"  and %@(assertionResults.toSeq) { ar =>
          assertAssertionResultPassed("truism")(ar(0))      and
          assertAssertionResultFailed("falsism")(ar(1))     and
          assertAssertionResultThrew(
            "error",
            _ =!=[NotImplementedError](_ =?= "an implementation is missing" | "assertion thrown")
          )(ar(2))
        }

      case other => failWith("SingleTestResult", other, "test result type")
    }
  }

  val t6 = test("mixed sequential test - stops on failure") {
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

        testName =?= "success + fails + stops" | "test name"         and
        failedAssertionName =?= "falsism" | "failed assertion"       and
        failedAssertionName =?= aName | "failure assertion"          and
        errors =?= oneOrMore("false != true") | "failure reason"     and
        passed.length =?= 1 | "no of passed assertions"              and
        assertSequentialPass("truism")(passed(0))                    and
        notRun.length =?= 1 | "no of notRun assertions"              and
        notRun(0).name.value =?= "error" | "not run assertion name"

      case other => failWith("CompositeTestResult/StoppedOnFirstFailed/SequentialFail", other, "test result type")
    }
  }

  val t7 = test("mixed sequential test - stops on error") {
    val tx = test("success + error + stops") {
      true =?= true  | "truism"  and
      true =?= ???   | "error"   and
      false =?= true | "falsism" seq()
    }

    val result = Boon.runTest(tx)

    result match {
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

      case other => failWith("CompositeTestResult/StoppedOnFirstFailed/SequentialThrew", other, "test result type")
    }
  }

  val t8 = test("successful suite") {

    val tx1 = test("boolean test") {
      true =?= true | "truism"
    }

    val tx2 = test("int test") {
      2 + 2 =?= 4 | "addition"
    }

    val sx = createSuite("My very own suite")(oneOrMore(tx1, tx2))

    val result = Boon.runSuite(sx)

    result match {
      case SuiteResult(DeferredSuite(SuiteName(name), NonEmptySeq(tx1, tx2)), testResults) =>
        name =?= "My very own suite" | "suite name"         and
        testResults.length =?= 2     | "no of test results" and %@(testResults.toSeq) { tr =>
          %@(tr(0), "test1")(assertSingleTestResult("boolean test", "truism"))  and
          %@(tr(1), "test2")(assertSingleTestResult("int test", "addition"))
        }

       case other => failWith("SuiteResult with 2 tests", other, "suite type")
    }
  }

  private def failWith[A](expected: String, other: => A, assertionName: String): AssertionData =
    fail(s"Expected $expected but got $other") | assertionName

  private def assertAssertionResultPassed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(aName), context, _))) =>
          aName =?= assertionName | s"assertion name of '$assertionName'"
        case other => failWith("SingleAssertionResult/AssertionResultPassed", other, "assertion result type")
      }
    }

  private def assertAssertionResultFailed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(aName), _, _, _), errors))) =>
          aName =?= assertionName | s"assertion name of $assertionName"
        case other => failWith("SingleAssertionResult/AssertionResultFailed", other, "assertion result type")
      }
    }

  private def assertAssertionResultThrew(assertionName: String, f: Throwable => AssertionData)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultThrew(AssertionThrow(AssertionName(aName), throwable, _))) =>
          aName =?= assertionName | s"assertion name of $assertionName" and
          f(throwable)
        case other => failWith("SingleAssertionResult/AssertionResultThrew", other, "assertion result type")
      }
    }

  private def assertSequentialPass(assertionName: String)(sp: SequentialPass): AssertionData = {
    sp.name.value =?= assertionName | s"assertion name of $assertionName"
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

  override val tests = oneOrMore(t1, t2, t3, t4, t5, t6, t7, t8)
}