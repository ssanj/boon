package boon

final case class Assertion(name: String, testable: Testable)

final case class Test(name: String, assertions: NonEmptySeq[Assertion])

//TODO: make this correct-by-construction: use NonEmptyList
final case class Suite(name: String, tests: NonEmptySeq[Test])
