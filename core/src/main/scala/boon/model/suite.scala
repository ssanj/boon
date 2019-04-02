package boon
package model

final case class TestName(value: String)

final case class DeferredTest(name: TestName, assertions: NonEmptySeq[Assertion], combinator: AssertionCombinator)
final case class ThrownTest(name: TestName, error: Throwable, loc: SourceLocation)

sealed trait Test
final case class SuccessfulTest(test: DeferredTest) extends Test
final case class UnsuccessfulTest(test: ThrownTest) extends Test
final case class IgnoredTest(test: TestName) extends Test

sealed trait CompositeTestResultState
final case class AllPassed(name: TestName, pass: NonEmptySeq[SequentialPass]) extends CompositeTestResultState
final case class StoppedOnFirstFailed(name: TestName, value: FirstFailed) extends CompositeTestResultState

sealed trait TestResult
final case class SingleTestResult(test: DeferredTest, assertionResults: NonEmptySeq[AssertionResult])  extends TestResult
final case class CompositeTestResult(value: CompositeTestResultState) extends TestResult
final case class TestThrewResult(test: ThrownTest) extends TestResult
final case class TestIgnoredResult(name: TestName) extends TestResult

sealed trait TestState

object TestState {
  case object Passed  extends TestState
  case object Failed  extends TestState
  case object Ignored  extends TestState

  implicit val assertionStateBoonType = BoonType.defaults[TestState]
}

sealed trait SuiteState

object SuiteState {
  case object Passed extends SuiteState
  case object Failed extends SuiteState

  implicit val assertionStateBoonType = BoonType.defaults[SuiteState]
}

object TestResult {

  def testResultToTestState(tr: TestResult): TestState = tr match {
    case SingleTestResult(_, ar) =>
      val failedOp = ar.map(AssertionResult.assertionResultToAssertionState).find {
        case AssertionState.Failed => true
        case AssertionState.Passed => false
      }

      failedOp.fold[TestState](TestState.Passed)(_ => TestState.Failed)

    case CompositeTestResult(_: AllPassed) => TestState.Passed

    case CompositeTestResult(_: StoppedOnFirstFailed) => TestState.Failed

    case _: TestThrewResult => TestState.Failed

    case _: TestIgnoredResult => TestState.Ignored
  }

  import stats.AssertionCount
  import stats.StatusCount

  def testResultToAssertionCount(tr: TestResult): AssertionCount = tr match {
    case SingleTestResult(_, ar) =>
      val triple =
        ar.map(AssertionResult.assertionResultToAssertionState).foldLeft((0,0,0))({
          case (acc, AssertionState.Failed) => (acc._1, acc._2 + 1, acc._3) //it's easier to copy tuples than case classes with nested fields
          case (acc, AssertionState.Passed) =>  (acc._1 + 1, acc._2, acc._3)
        })

      AssertionCount(StatusCount(triple._1, triple._2), triple._3)

    case CompositeTestResult(AllPassed(_, pass)) =>
      AssertionCount(StatusCount(pass.length, 0), 0)

    case CompositeTestResult(StoppedOnFirstFailed(_, FirstFailed(_, _, passed, notRun))) =>
      AssertionCount(StatusCount(passed.length, 1), notRun.length)

    case _: TestThrewResult => Monoid[AssertionCount].mempty

    case _: TestIgnoredResult => Monoid[AssertionCount].mempty
  }

  def testName(tr: TestResult): TestName = tr match {
    case SingleTestResult(DeferredTest(name, _, _), _)      => name
    case CompositeTestResult(AllPassed(name, _))            => name
    case CompositeTestResult(StoppedOnFirstFailed(name, _)) => name
    case TestThrewResult(ThrownTest(name, _, _))            => name
    case TestIgnoredResult(name)                            => name
  }
}

final case class SuiteName(value: String)

final case class DeferredSuite(name: SuiteName, tests: NonEmptySeq[Test])

final case class SuiteResult(suite: DeferredSuite, testResults: NonEmptySeq[TestResult])

object SuiteResult {

  def suiteResultToSuiteState(sr: SuiteResult): SuiteState = {
    val failedOp = sr.testResults.map(TestResult.testResultToTestState).find {
      case TestState.Failed  => true
      case TestState.Passed  => false
      case TestState.Ignored => false
    }

   failedOp.fold[SuiteState](SuiteState.Passed)(_ => SuiteState.Failed)
  }
}
