package boon

final case class Test(name: String, testable: Testable)

final case class Suite(name: String, tests: Seq[Test])

  final case class Success(name: String)
  final case class Failure(name: String, error: String)

sealed trait TestResult

object TestResult {
  def isSuccess(tr: TestResult): Boolean = tr match {
    case ts: TestSuccess => true
    case tf: TestFailure => false
  }

  def isFailure(tr: TestResult): Boolean = !isSuccess(tr)
}

final case class TestSuccess(value: Success) extends TestResult
final case class TestFailure(value: Failure) extends TestResult

sealed trait SuiteResult
//Should these be NonEmptyLists?
final case class AllPassed(name: String, passed: Seq[Success]) extends SuiteResult
final case class SomePassed(name: String, passed: Seq[Success], failed: Seq[Failure]) extends SuiteResult
final case class AllFailed(name: String, failed: Seq[Failure]) extends SuiteResult
final case class NoTests(name: String) extends SuiteResult

