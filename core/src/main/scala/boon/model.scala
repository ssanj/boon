package boon

sealed trait Passable
case object Passed extends Passable
case object Failed extends Passable

object Passable {
  def hasPassed(passable: Passable): Boolean = passable == Passed

  def hasFailed(passable: Passable): Boolean = !hasPassed(passable)
}

final case class Defer[A](value: () => A) {
  def map[B](f: A => B): Defer[B] = Defer(() => f(value()))

  def flatMap[B](f: A => Defer[B]): Defer[B] = Defer(() => f(value()).value())

  def run(): A = value()
}

final case class AssertionName(value: String)

sealed trait Assertion
final case class SingleAssertion(name: AssertionName, testable: Defer[Testable], context: Map[String, String], location: SourceLocation) extends Assertion
final case class CompositeAssertion(name: AssertionName, assertions: NonEmptySeq[Assertion], context: Map[String, String], location: SourceLocation) extends Assertion
// final case class Assertion(name: AssertionName, testable: Defer[Testable], context: Map[String, String], location: SourceLocation)
final case class AssertionError(assertion: Assertion, error: String)
final case class AssertionThrow(name: AssertionName, value: Throwable, location: SourceLocation)

object Assertion {
  def assertionName(assertion: Assertion): AssertionName = assertion match {
    case SingleAssertion(name, _, _, _)    => name
    case CompositeAssertion(name, _, _, _) => name
  }

  def assertionContext(assertion: Assertion): Map[String, String] = assertion match {
    case SingleAssertion(_, _, ctx, _)    => ctx
    case CompositeAssertion(_, _, ctx, _) => ctx
  }

  def assertionLocation(assertion: Assertion): SourceLocation = assertion match {
    case SingleAssertion(_, _, _, loc)    => loc
    case CompositeAssertion(_, _, _, loc) => loc
  }
}

sealed trait AssertionFailure
final case class SingleAssertionFailed(value: AssertionError) extends AssertionFailure
final case class SingleAssertionThrew(value: AssertionThrow) extends AssertionFailure
final case class CompositeAssertionFailed(name: AssertionName, failed: Either[CompositeFail, CompositeThrew], passed: Seq[CompositePass], notRun: Seq[CompositeNotRun]) extends AssertionFailure

sealed trait AssertionResult extends Product with Serializable

final case class CompositeNotRun(name: AssertionName)
final case class CompositePass(name: AssertionName)
final case class CompositeFail(value: AssertionError)
final case class CompositeThrew(value: AssertionThrow)

final case class CompositeAssertionAllPassed(name: AssertionName, pass: NonEmptySeq[CompositePass]) extends AssertionResult
final case class CompositeAssertionFirstFailed(name: AssertionName, failed: Either[CompositeFail, CompositeThrew], passed: Seq[CompositePass], notRun: Seq[CompositeNotRun]) extends AssertionResult

final case class AssertionPassed(assertion: Assertion) extends AssertionResult
final case class AssertionFailed(value: AssertionError) extends AssertionResult
final case class AssertionThrew(value: AssertionThrow) extends AssertionResult

final case class TestName(value: String)
final case class DeferredTest(name: TestName, assertions: NonEmptySeq[Assertion])
final case class TestResult(test: DeferredTest, assertionResults: NonEmptySeq[AssertionResult])

final case class SuiteName(value: String)
final case class DeferredSuite(name: SuiteName, tests: NonEmptySeq[DeferredTest])
final case class SuiteResult(suite: DeferredSuite, testResults: NonEmptySeq[TestResult])

sealed trait EqualityType
case object IsEqual extends EqualityType
case object IsNotEqual extends EqualityType

object EqualityType {
  final case class FoldSyntax(et: EqualityType) {
    def fold[A](isNotEqual: => A, isEqual: => A): A = et match {
      case IsEqual    => isEqual
      case IsNotEqual => isNotEqual
    }
  }

  implicit def foldEqualityType(et: EqualityType): FoldSyntax = FoldSyntax(et)
}

sealed trait FailableAssertion
final case class FailedAssertion(reason: String) extends FailableAssertion
object PassedAssertion extends FailableAssertion

sealed trait AssertionCombinator
case object Independent extends AssertionCombinator
case object Sequential extends AssertionCombinator


