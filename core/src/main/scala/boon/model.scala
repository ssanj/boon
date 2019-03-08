package boon

sealed trait Passable
case object Passed extends Passable
case object Failed extends Passable

final case class Defer[A](value: () => A) {
  def map[B](f: A => B): Defer[B] = Defer(() => f(value()))

  def flatMap[B](f: A => Defer[B]): Defer[B] = Defer(() => f(value()).value())

  def run(): A = value()
}

final case class AssertionName(value: String)
final case class Assertion(name: AssertionName, testable: Defer[Testable], context: Map[String, String], location: SourceLocation)
final case class AssertionError(assertion: Assertion, error: String)

sealed trait AssertionResult extends Product with Serializable
final case class AssertionPassed(assertion: Assertion) extends AssertionResult
final case class AssertionFailed(value: AssertionError) extends AssertionResult
final case class AssertionThrew(name: AssertionName, value: Throwable, location: SourceLocation) extends AssertionResult

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

