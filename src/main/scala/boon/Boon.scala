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
    if (testable.equality.eql(value1, value2)) AssertionSuccess(AssertionResult.Success(AssertionResult.AssertionName(assertion.name)))
    else AssertionFailure(AssertionResult.Failure(AssertionResult.AssertionName(assertion.name), testable.difference.diff(value1, value2)))
  }

  def runTest(test: Test): TestResult = test match {
    case Test(name, assertions) =>
      val theseResults: These[NonEmptySeq[AssertionResult.Failure], NonEmptySeq[AssertionResult.Success]] =
        assertions.map(runAssertion).partition {
          case AssertionFailure(value) => Left(value)
          case AssertionSuccess(value) => Right(value)
        }

      theseResults match {
        case OnlyLeft(values) => TestFailure(TestResult.Failure(TestResult.TestName(test.name), values))
        case OnlyRight(values) => TestSuccess(TestResult.Success(TestResult.TestName(test.name), values))
        case Both(lefts, rights) => TestMixed(TestResult.Mixed(TestResult.TestName(test.name), rights, lefts))
      }
  }

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(suiteLike.suite)

  def runSuite(suite: Suite): SuiteResult = {
    val testResults: NonEmptySeq[TestResult] = suite.tests.map(runTest)

    val triple = testResults.partition2[TestResult.Failure, TestResult.Mixed, TestResult.Success] {
      case TestFailure(failures) => Left(failures)
      case TestMixed(mixed) => Right(Left(mixed))
      case TestSuccess(successes) => Right(Right(successes))
    }

    triple match {
      case Triple.LeftOnly(values) => SuiteResult.Failure(SuiteResult.SuiteName(suite.name), values)
      case Triple.Middle(values) => SuiteResult.Mixed(SuiteResult.SuiteName(suite.name), values)
      case Triple.RightOnly(values) => SuiteResult.Success(SuiteResult.SuiteName(suite.name), values)
    }
  }
}

