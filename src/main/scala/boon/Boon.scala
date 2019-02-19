package boon

object Boon {

  def testable[A](a1: A, a2: A)(implicit E: Equality[A], D: Difference[A]): Testable = new Testable {
    type Actual = A
    val value1: Actual = a1
    val value2: Actual = a2
    val equality = E
    val difference = D
  }

  def test(name: String)(assertions: NonEmptySeq[Assertion]): Test = Test(TestName(name), assertions)


  def defineAssertion[A](name: String, gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Assertion =
    defineAssertionWithContext[A](name, gen, Map.empty[String, String])

  def defineAssertionWithContext[A](name: String, gen: => (A, A), context: Map[String, String])(implicit E: Equality[A], D: Difference[A]): Assertion =
    Assertion(AssertionName(name), {
      val (a1, a2) = gen
      testable[A](a1, a2)
    }, context)

  def runAssertion(assertion: Assertion): AssertionResult = {
    val testable = assertion.testable
    val value1 = testable.value1
    val value2 = testable.value2
    if (testable.equality.eql(value1, value2)) AssertionPassed(assertion)
    else AssertionFailed(AssertionError(assertion, testable.difference.diff(value1, value2)))
  }

  def runTest(test: Test): TestResult =
      TestResult(test, test.assertions.map(runAssertion))

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(suiteLike.suite)

  def runSuite(suite: Suite): SuiteResult =
    SuiteResult(suite, suite.tests.map(runTest))

  def assertionResultToPassable(ar: AssertionResult): Passable = ar match {
    case _: AssertionPassed => Passed
    case _: AssertionFailed => Failed
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

