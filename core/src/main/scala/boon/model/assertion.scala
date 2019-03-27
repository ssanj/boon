package boon
package model

final case class AssertionTriple(name: AssertionName, context: Map[String, String], location: SourceLocation)

object AssertionTriple {

  def from(assertion: Assertion): AssertionTriple = AssertionTriple(assertion.name, assertion.context, assertion.location)
}

final case class AssertionName(value: String)

final case class Assertion(name: AssertionName, testable: Defer[Testable], context: Map[String, String], location: SourceLocation)

final case class AssertionError(assertion: Assertion, errors: NonEmptySeq[String])
final case class AssertionThrow(name: AssertionName, value: Throwable, location: SourceLocation)
final case class FirstFailed(name: AssertionName, failed: Either[SequentialFail, SequentialThrew], passed: Seq[SequentialPass], notRun: Seq[SequentialNotRun])

sealed trait AssertionFailure
final case class AssertionFailed(value: AssertionError) extends AssertionFailure
final case class AssertionThrew(value: AssertionThrow) extends AssertionFailure

sealed trait AssertionResult extends Product with Serializable

object AssertionResult {
  def assertionNameFromResult(ar: AssertionResult): AssertionName = ar match {
    case SingleAssertionResult(AssertionResultPassed(AssertionTriple(name, _, _))) => name
    case SingleAssertionResult(AssertionResultFailed(AssertionError(assertion, _))) => assertion.name
    case SingleAssertionResult(AssertionResultThrew(AssertionThrow(name, _, _))) => name
  }

  def assertionResultToAssertionState(ar: AssertionResult): AssertionState = ar match {
    case SingleAssertionResult(_: AssertionResultPassed)         => AssertionState.Passed
    case SingleAssertionResult(_: AssertionResultFailed)         => AssertionState.Failed
    case SingleAssertionResult(_: AssertionResultThrew )         => AssertionState.Failed
  }
}

final case class TestData(assertions: NonEmptySeq[Assertion], combinator: AssertionCombinator)

final case class SequentialNotRun(name: AssertionName)
final case class SequentialPass(name: AssertionName)
final case class SequentialFail(value: AssertionError)
final case class SequentialThrew(value: AssertionThrow)

sealed trait AssertionState

object AssertionState {
  case object Passed extends AssertionState
  case object Failed extends AssertionState

  implicit val assertionStateBoonType = BoonType.defaults[AssertionState]
}

sealed trait AssertionResultState
final case class AssertionResultPassed(value: AssertionTriple) extends AssertionResultState
final case class AssertionResultFailed(value: AssertionError) extends AssertionResultState
final case class AssertionResultThrew(value: AssertionThrow) extends AssertionResultState

final case class SingleAssertionResult(value: AssertionResultState) extends AssertionResult


sealed trait AssertionCombinator
case object Independent extends AssertionCombinator
case object Sequential extends AssertionCombinator

sealed trait FailableAssertion
final case class FailedAssertion(reason: String) extends FailableAssertion
object PassedAssertion extends FailableAssertion

