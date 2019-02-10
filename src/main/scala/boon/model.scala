package boon

final case class TestName(value: String)

final case class SuiteName(value: String)

final case class AssertionName(value: String)

final case class Assertion(name: AssertionName, testable: Testable)

final case class Test(name: TestName, assertions: NonEmptySeq[Assertion])

final case class Suite(name: SuiteName, tests: NonEmptySeq[Test])
