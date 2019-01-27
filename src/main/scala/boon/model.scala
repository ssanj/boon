package boon

final case class Assertion(name: String, testable: Testable)

sealed trait Test
final case class SingleAssertionTest(name: String, assertion: Assertion) extends Test
//TODO: make this correct-by-construction: use NonEmptyList
final case class MultiAssertionTest(name: String, assertions: Seq[Assertion]) extends Test

//TODO: make this correct-by-construction: use NonEmptyList
final case class Suite(name: String, tests: Seq[Test])
