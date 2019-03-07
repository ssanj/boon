package boon

abstract class SuiteLike(val suiteName: String) {

  def tests: NonEmptySeq[DeferredTest]

  def suite: DeferredSuite = DeferredSuite(SuiteName(suiteName), tests)
}