package boon

final case class Test(name: String, testable: Testable)

final case class Suite(name: String, tests: Seq[Test])

sealed trait TestResult

object TestResult {

  final case class Success(name: String)
  final case class Failure(name: String, error: String)

  def isSuccess(tr: TestResult): Boolean = tr match {
    case ts: TestSuccess => true
    case tf: TestFailure => false
  }

  def isFailure(tr: TestResult): Boolean = !isSuccess(tr)
}

final case class TestSuccess(value: TestResult.Success) extends TestResult
final case class TestFailure(value: TestResult.Failure) extends TestResult

sealed trait SuiteResult
//Should these be NonEmptyLists?
final case class AllPassed(name: String, passed: Seq[TestResult.Success]) extends SuiteResult
final case class SomePassed(name: String, passed: Seq[TestResult.Success], failed: Seq[TestResult.Failure]) extends SuiteResult
final case class AllFailed(name: String, failed: Seq[TestResult.Failure]) extends SuiteResult
final case class NoTests(name: String) extends SuiteResult

