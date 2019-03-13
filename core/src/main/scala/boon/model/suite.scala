package boon
package model

final case class TestName(value: String)

final case class DeferredTest(name: TestName, assertions: NonEmptySeq[Assertion], combinator: AssertionCombinator)

sealed trait CompositeTestResultState
final case class AllPassed(name: TestName, pass: NonEmptySeq[CompositePass]) extends CompositeTestResultState
final case class StoppedOnFirstFailed(name: TestName, value: FirstFailed) extends CompositeTestResultState

sealed trait TestResult
//TODO: Come up with a better name for these
//example IndependentAssertionTestResult
//example SequentialAssertionTestResult
final case class SingleTestResult(test: DeferredTest, assertionResults: NonEmptySeq[AssertionResult])  extends TestResult
final case class CompositeTestResult(value: CompositeTestResultState) extends TestResult

object TestResult {

  def testResultToPassable(tr: TestResult): Passable = tr match {
    case SingleTestResult(_, ar) =>
      val failedOp = ar.map(AssertionResult.assertionResultToPassable).find {
        case Failed => true
        case Passed => false
      }

      failedOp.fold[Passable](Passed)(_ => Failed)

    case CompositeTestResult(_: AllPassed) => Passed

    case CompositeTestResult(_: StoppedOnFirstFailed) => Failed
  }

  def testName(tr: TestResult): TestName = tr match {
    case SingleTestResult(DeferredTest(name, _, _), _)      => name
    case CompositeTestResult(AllPassed(name, _))            => name
    case CompositeTestResult(StoppedOnFirstFailed(name, _)) => name
  }
}

final case class SuiteName(value: String)

final case class DeferredSuite(name: SuiteName, tests: NonEmptySeq[DeferredTest])

final case class SuiteResult(suite: DeferredSuite, testResults: NonEmptySeq[TestResult])

object SuiteResult {

  def suiteResultToPassable(sr: SuiteResult): Passable = {
    val failedOp = sr.testResults.map(TestResult.testResultToPassable).find {
      case Failed => true
      case Passed => false
    }

   failedOp.fold[Passable](Passed)(_ => Failed)
  }
}
