package boon

sealed trait AssertionResult

object AssertionResult {

  final case class AssertionName(name: String)

  final case class Success(name: AssertionName)
  final case class Failure(name: AssertionName, error: String)

  def isSuccess(tr: AssertionResult): Boolean = tr match {
    case ts: AssertionSuccess => true
    case tf: AssertionFailure => false
  }

  def isFailure(tr: AssertionResult): Boolean = !isSuccess(tr)
}

final case class AssertionSuccess(value: AssertionResult.Success) extends AssertionResult
final case class AssertionFailure(value: AssertionResult.Failure) extends AssertionResult


sealed trait TestResult

object TestResult {
  final case class TestName(name: String)
  final case class Success(name: TestName, successes: NonEmptySeq[AssertionResult.Success])
  final case class Mixed(name: TestName, successes: NonEmptySeq[AssertionResult.Success], failures: NonEmptySeq[AssertionResult.Failure])
  final case class Failure(name: TestName, successes: NonEmptySeq[AssertionResult.Failure])
}

final case class TestSuccess(value: TestResult.Success) extends TestResult
final case class TestMixed(value: TestResult.Mixed) extends TestResult
final case class TestFailure(value: TestResult.Failure) extends TestResult


sealed trait SuiteResult

object SuiteResult {
  final case class SuiteName(name: String)
  final case class Success(name: SuiteName, successes: NonEmptySeq[TestResult.Success]) extends SuiteResult
  final case class Mixed(name: SuiteName, mixed: NonEmptySeq[TestResult.Mixed]) extends SuiteResult
  final case class Failure(name: SuiteName, failures: NonEmptySeq[TestResult.Failure]) extends SuiteResult
}

final case class SuiteSuccess(value: SuiteResult.Success) extends SuiteResult
final case class SuiteMixed(value: SuiteResult.Mixed) extends SuiteResult
final case class SuiteFailure(value: SuiteResult.Failure) extends SuiteResult
