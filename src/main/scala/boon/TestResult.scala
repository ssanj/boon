package boon

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
