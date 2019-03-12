package boon
package model

final case class AssertionTriple(name: AssertionName, context: Map[String, String], location: SourceLocation)

object AssertionTriple {

  def from(assertion: Assertion): AssertionTriple = assertion match {
    case SingleAssertion(name, _, context, location) => AssertionTriple(name, context, location)
    case CompositeAssertion(name, _, location) => AssertionTriple(name, noContext, location)
  }
}

final case class AssertionName(value: String)

sealed trait Assertion
final case class SingleAssertion(name: AssertionName, testable: Defer[Testable], context: Map[String, String], location: SourceLocation) extends Assertion
final case class CompositeAssertion(name: AssertionName, assertions: NonEmptySeq[Assertion], location: SourceLocation) extends Assertion
final case class AssertionError(assertion: Assertion, error: String)
final case class AssertionThrow(name: AssertionName, value: Throwable, location: SourceLocation)
final case class FirstFailed(name: AssertionName, failed: Either[CompositeFail, CompositeThrew], passed: Seq[CompositePass], notRun: Seq[CompositeNotRun])

object Assertion {
  def assertionName(assertion: Assertion): AssertionName = assertion match {
    case SingleAssertion(name, _, _, _) => name
    case CompositeAssertion(name, _, _) => name
  }

  def assertionContext(assertion: Assertion): Map[String, String] = assertion match {
    case SingleAssertion(_, _, ctx, _) => ctx
    case CompositeAssertion(_, _, _)   => noContext
  }

  def assertionLocation(assertion: Assertion): SourceLocation = assertion match {
    case SingleAssertion(_, _, _, loc) => loc
    case CompositeAssertion(_, _, loc) => loc
  }
}

sealed trait AssertionFailure
final case class SingleAssertionFailed(value: AssertionError) extends AssertionFailure
final case class SingleAssertionThrew(value: AssertionThrow) extends AssertionFailure
final case class CompositeAssertionFailed(value: FirstFailed) extends AssertionFailure

sealed trait AssertionResult extends Product with Serializable

object AssertionResult {
  def assertionNameFromResult(ar: AssertionResult): AssertionName = ar match {
    case SingleAssertionResult(AssertionPassed(AssertionTriple(name, _, _))) => name
    case SingleAssertionResult(AssertionFailed(AssertionError(assertion, _))) => Assertion.assertionName(assertion)
    case SingleAssertionResult(AssertionThrew(AssertionThrow(name, _, _))) => name
    case CompositeAssertionResult(AllPassed(name, _)) => name
    case CompositeAssertionResult(StoppedOnFirstFailed(FirstFailed(name, _, _, _))) => name
  }

  def assertionResultToPassable(ar: AssertionResult): Passable = ar match {
    case SingleAssertionResult(_: AssertionPassed)         => Passed
    case SingleAssertionResult(_: AssertionFailed)         => Failed
    case SingleAssertionResult(_: AssertionThrew )         => Failed
    case CompositeAssertionResult(_: AllPassed)            => Passed
    case CompositeAssertionResult(_: StoppedOnFirstFailed) => Failed
  }

}

final case class CompositeNotRun(name: AssertionName)
final case class CompositePass(name: AssertionName)
final case class CompositeFail(value: AssertionError)
final case class CompositeThrew(value: AssertionThrow)

sealed trait CompositeAssertionResultState
final case class AllPassed(name: AssertionName, pass: NonEmptySeq[CompositePass]) extends CompositeAssertionResultState
final case class StoppedOnFirstFailed(value: FirstFailed) extends CompositeAssertionResultState

sealed trait SingleAssertionState
final case class AssertionPassed(value: AssertionTriple) extends SingleAssertionState
final case class AssertionFailed(value: AssertionError) extends SingleAssertionState
final case class AssertionThrew(value: AssertionThrow) extends SingleAssertionState

final case class SingleAssertionResult(value: SingleAssertionState) extends AssertionResult
final case class CompositeAssertionResult(value: CompositeAssertionResultState) extends AssertionResult

sealed trait AssertionCombinator
case object Independent extends AssertionCombinator
case object Sequential extends AssertionCombinator

sealed trait FailableAssertion
final case class FailedAssertion(reason: String) extends FailableAssertion
object PassedAssertion extends FailableAssertion

