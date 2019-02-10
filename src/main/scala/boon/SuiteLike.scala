package boon

abstract class SuiteLike(val suiteName: String)(tests: NonEmptySeq[Test]) {

  def suite: Suite = Suite(SuiteName(suiteName), tests)

  //we need another test method for multi assertion tests
}