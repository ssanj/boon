package boon

object Boon {

  def testable[A](a1: A, a2: A)(implicit E: Equality[A], D: Difference[A]): Testable = new Testable {
    type Actual = A
    val value1: Actual = a1
    val value2: Actual = a2
    val equality = E
    val difference = D
  }

  def defineTest[A](name: String, gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Test =
    //TODO: Can we do without the duplicate 'name' in Test and Assertion
    Test(
      name,
      NonEmptySeq.nes(Assertion(name, {
        val (a1, a2) = gen
        testable[A](a1, a2)
      }))
    )

  def defineAssertion[A](name: String, gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Assertion =
    Assertion(name, {
      val (a1, a2) = gen
      testable[A](a1, a2)
    })


  def runAssertion(assertion: Assertion): AssertionResult = {
    val testable = assertion.testable
    val value1 = testable.value1
    val value2 = testable.value2
    if (testable.equality.eql(value1, value2)) AssertionPassed(AssertionResult.Passed(AssertionName(assertion.name)))
    else AssertionFailed(AssertionResult.Failed(AssertionName(assertion.name), testable.difference.diff(value1, value2)))
  }

  def runTest(test: Test): TestResult = test match {
    case Test(name, assertions) =>
      val theseResults: These[NonEmptySeq[AssertionResult.Failed], NonEmptySeq[AssertionResult.Passed]] =
        assertions.map(runAssertion).partition {
          case AssertionFailed(value) => Left(value)
          case AssertionPassed(value) => Right(value)
        }

      theseResults match {
        case These.OnlyLeft(values) => TestFailed(TestResult.Failed(test, values, None))
        case These.OnlyRight(values) => TestPassed(TestResult.Passed(test, values))
        case These.Both(lefts, rights) => TestFailed(TestResult.Failed(test, lefts, Some(rights)))
      }
  }

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(suiteLike.suite)

  def runSuite(suite: Suite): SuiteResult = {
    val testResults: NonEmptySeq[TestResult] = suite.tests.map(runTest)

    val theseResults: These[NonEmptySeq[TestResult.Failed], NonEmptySeq[TestResult.Passed]] =
      testResults.partition {
        case TestFailed(value) => Left(value)
        case TestPassed(value) => Right(value)
      }

    theseResults match {
      case These.OnlyLeft(values)    => SuiteFailed(SuiteResult.Failed(suite, values, None))
      case These.OnlyRight(values)   => SuitePassed(SuiteResult.Passed(suite, values))
      case These.Both(lefts, rights) => SuiteFailed(SuiteResult.Failed(suite, lefts, Some(rights)))
    }
  }
}

