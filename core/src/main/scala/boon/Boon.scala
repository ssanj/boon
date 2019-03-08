package boon

import scala.util.Try

object Boon {

  def testable[A](a1: Defer[A], a2: Defer[A], et: EqualityType)(implicit E: Equality[A], D: Difference[A]): Defer[Testable] = {
    val t = () => new Testable {
      type Actual = A
      val value1: Defer[Actual] = a1
      val value2: Defer[Actual] = a2
      val equality = E
      val difference = D
      val equalityType = et
    }

    Defer[Testable](t)
  }

  def defineAssertion[A](name: => String, gen: (Defer[A], Defer[A]), equalityType: EqualityType)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): Assertion =
    defineAssertionWithContext[A](name, gen, equalityType, Map.empty[String, String])

  def defineAssertionWithContext[A](name: => String, gen: (Defer[A], Defer[A]), equalityType: EqualityType, context: Map[String, String])(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): Assertion =
    Assertion(AssertionName(name), {
      val (a1, a2) = gen
      testable[A](a1, a2, equalityType)
    }, context, loc)

  def runAssertion(assertion: Assertion): AssertionResult = {
    Try {
      val testable = assertion.testable.run()
      val value1 = testable.value1.run()
      val value2 = testable.value2.run()

      val eqFunc = testable.equalityType.fold(testable.equality.neql _, testable.equality.eql _)

      if (eqFunc(value1, value2)) AssertionPassed(assertion)
      else AssertionFailed(AssertionError(assertion, testable.difference.diff(value1, value2)))
    }.fold(AssertionThrew(assertion.name, _, assertion.location), identity _)
  }

  def runTest(dTest: DeferredTest): TestResult = {
    val assertionResults = dTest.assertions.map(runAssertion)
    TestResult(dTest, assertionResults)
  }

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(suiteLike.suite)

  def runSuite(dSuite: DeferredSuite): SuiteResult = {
    val testResults = dSuite.tests.map(runTest)
    SuiteResult(dSuite, testResults)
  }

  def assertionResultToPassable(ar: AssertionResult): Passable = ar match {
    case _: AssertionPassed => Passed
    case _: AssertionFailed => Failed
    case _: AssertionThrew  => Failed
  }

  def testResultToPassable(tr: TestResult): Passable = {
    val failedOp = tr.assertionResults.map(assertionResultToPassable).find {
      case Failed => true
      case Passed => false
    }

    failedOp.fold[Passable](Passed)(_ => Failed)
  }

  def suiteResultToPassable(sr: SuiteResult): Passable = {
    val failedOp = sr.testResults.map(testResultToPassable).find {
      case Failed => true
      case Passed => false
    }

   failedOp.fold[Passable](Passed)(_ => Failed)
  }
}

