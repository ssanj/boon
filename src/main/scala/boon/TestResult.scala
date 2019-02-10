package boon

sealed trait AssertionResult

object AssertionResult {
  final case class Passed(name: AssertionName)
  final case class Failed(name: AssertionName, error: String)

}

final case class AssertionPassed(value: AssertionResult.Passed) extends AssertionResult
final case class AssertionFailed(value: AssertionResult.Failed) extends AssertionResult

sealed trait TestResult

object TestResult {
  final case class Passed(test: Test, successes: NonEmptySeq[AssertionResult.Passed])
  final case class Failed(test: Test, failures: NonEmptySeq[AssertionResult.Failed], opSuccesses: Option[NonEmptySeq[AssertionResult.Passed]])
}

final case class TestPassed(value: TestResult.Passed) extends TestResult
final case class TestFailed(value: TestResult.Failed) extends TestResult


sealed trait SuiteResult

object SuiteResult {
  final case class Passed(suite: Suite, successes: NonEmptySeq[TestResult.Passed])
  final case class Failed(suite: Suite, failures: NonEmptySeq[TestResult.Failed], opSuccesses: Option[NonEmptySeq[TestResult.Passed]])
}

final case class SuitePassed(value: SuiteResult.Passed) extends SuiteResult
final case class SuiteFailed(value: SuiteResult.Failed) extends SuiteResult
