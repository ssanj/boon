package boon
package model

final case class TestName(value: String)

final case class DeferredTest(name: TestName, assertions: NonEmptySeq[Assertion], combinator: AssertionCombinator)

sealed trait CompositeTestResultState
final case class AllPassed(name: TestName, pass: NonEmptySeq[SequentialPass]) extends CompositeTestResultState
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

  import stats.AssertionCount
  import stats.StatusCount

  def testResultToAssertionCount(tr: TestResult): AssertionCount = tr match {
    case SingleTestResult(_, ar) =>
      val triple =
        ar.map(AssertionResult.assertionResultToPassable).foldLeft((0,0,0))({
          case (acc, Failed) => (acc._1, acc._2 + 1, acc._3) //it's easier to copy tuples than case classes with nested fields
          case (acc, Passed) =>  (acc._1 + 1, acc._2, acc._3)
        })

      AssertionCount(StatusCount(triple._1, triple._2), triple._3)

    case CompositeTestResult(AllPassed(_, pass)) =>
      AssertionCount(StatusCount(pass.length, 0), 0)

    case CompositeTestResult(StoppedOnFirstFailed(_, FirstFailed(_, _, passed, notRun))) =>
      AssertionCount(StatusCount(passed.length, 1), notRun.length)
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
