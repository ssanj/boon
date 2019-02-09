package boon

final case class Assertion(name: String, testable: Testable)

final case class Test(name: String, assertions: NonEmptySeq[Assertion])

final case class Suite(name: String, tests: NonEmptySeq[Test])
