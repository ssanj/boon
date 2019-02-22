package boon

import scala.util.Try

object Boon {

  def testable[A](a1: Defer[A], a2: Defer[A])(implicit E: Equality[A], D: Difference[A]): Defer[Testable] = {
    val t = new Testable {
      type Actual = A
      val value1: Defer[Actual] = a1
      val value2: Defer[Actual] = a2
      val equality = E
      val difference = D
    }

    Defer(() => t)
  }

  def defineAssertion[A](name: => String, gen: (Defer[A], Defer[A]))(implicit E: Equality[A], D: Difference[A]): Defer[Assertion] =
    defineAssertionWithContext[A](name, gen, Map.empty[String, String])

  def defineAssertionWithContext[A](name: => String, gen: (Defer[A], Defer[A]), context: Map[String, String])(implicit E: Equality[A], D: Difference[A]): Defer[Assertion] =
    Defer(() => Assertion(AssertionName(name), {
      val (a1, a2) = gen
      testable[A](a1, a2)
    }, context))

  def runAssertion(dAssertion: Defer[Assertion]): AssertionResult = {
    val assertion = dAssertion.value()
    Try {
      val testable = assertion.testable.value()
      val value1 = testable.value1.value()
      val value2 = testable.value2.value()
      if (testable.equality.eql(value1, value2)) AssertionPassed(assertion)
      else AssertionFailed(AssertionError(assertion, testable.difference.diff(value1, value2)))
    }.fold(AssertionThrew(assertion.name, _), identity _)
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

