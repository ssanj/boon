package boon
package model

final case class TestName(value: String)

final case class DeferredTest(name: TestName, assertions: NonEmptySeq[Assertion])

final case class TestResult(test: DeferredTest, assertionResults: NonEmptySeq[AssertionResult])

object TestResult {

  def testResultToPassable(tr: TestResult): Passable = {
    val failedOp = tr.assertionResults.map(AssertionResult.assertionResultToPassable).find {
      case Failed => true
      case Passed => false
    }

    failedOp.fold[Passable](Passed)(_ => Failed)
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
