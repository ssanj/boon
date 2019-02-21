package boon

import scala.util.Try

object Boon {

  def testable[A](a1: A, a2: A)(implicit E: Equality[A], D: Difference[A]): Testable = new Testable {
    type Actual = A
    val value1: Actual = a1
    val value2: Actual = a2
    val equality = E
    val difference = D
  }

  def test(name: String)(assertions: NonEmptySeq[Defer[Assertion]]): DeferredTest = DeferredTest(TestName(name), assertions)


  def defineAssertion[A](name: String, gen: => (Defer[A], Defer[A]))(implicit E: Equality[A], D: Difference[A]): Defer[Assertion] =
    defineAssertionWithContext[A](name, gen, Map.empty[String, String])

  def defineAssertionWithContext[A](name: String, gen: => (Defer[A], Defer[A]), context: Map[String, String])(implicit E: Equality[A], D: Difference[A]): Defer[Assertion] =
    Defer(() => Assertion(AssertionName(name), {
      val (a1, a2) = gen
      testable[A](a1.value(), a2.value())
    }, context))

  def runAssertion(dAssertion: Defer[Assertion]): AssertionResult = Try {
    val assertion = dAssertion.value()
    val testable = assertion.testable
    val value1 = testable.value1
    val value2 = testable.value2
    if (testable.equality.eql(value1, value2)) AssertionPassed(assertion)
    else AssertionFailed(AssertionError(assertion, testable.difference.diff(value1, value2)))
  }.fold(AssertionThrew, identity _)

  def runTest(dTest: DeferredTest): TestResult = {
    val assertionResults = dTest.assertions.map(runAssertion)
    TestResult(
      Test(dTest.name,
        NonEmptySeq.one(
          Assertion(
            AssertionName("dummy"), testable(true, true), Map.empty[String, String]
          )
        )
      ), assertionResults)
  }

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(suiteLike.suite)

  def runSuite(dSuite: DeferredSuite): SuiteResult = {
    val testResults = dSuite.tests.map(runTest)
    SuiteResult(
      Suite(dSuite.name,
        dSuite.tests.map(dt =>
          Test(dt.name,
            NonEmptySeq.one(
              Assertion(
                AssertionName("dummy"), testable(true, true), Map.empty[String, String]
              )
            )
          )
        )
      ),
      testResults)
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

