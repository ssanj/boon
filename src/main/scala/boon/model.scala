package boon

sealed trait Passable
case object Passed extends Passable
case object Failed extends Passable

final case class Defer[A](value: () => A)

final case class AssertionName(value: String)
final case class Assertion(name: AssertionName, testable: Testable, context: Map[String, String])
final case class AssertionError(assertion: Assertion, error: String)

sealed trait AssertionResult
final case class AssertionPassed(assertion: Assertion) extends AssertionResult
final case class AssertionFailed(value: AssertionError) extends AssertionResult
final case class AssertionThrew(value: Throwable) extends AssertionResult

final case class TestName(value: String)
final case class Test(name: TestName, assertions: NonEmptySeq[Assertion])
final case class TestResult(test: Test, assertionResults: NonEmptySeq[AssertionResult])

final case class SuiteName(value: String)
final case class Suite(name: SuiteName, tests: NonEmptySeq[Test])
final case class SuiteResult(suite: Suite, testResults: NonEmptySeq[TestResult])

final case class DeferredTest(name: TestName, assertions: NonEmptySeq[Defer[Assertion]])
final case class DeferredSuite(name: SuiteName, tests: NonEmptySeq[DeferredTest])

final case class Not[A](value: A)
