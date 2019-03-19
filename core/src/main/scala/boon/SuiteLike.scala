package boon

import boon.model._

abstract class SuiteLike(val suiteName: String) {

  def tests: NonEmptySeq[Test]

  def suite: DeferredSuite = DeferredSuite(SuiteName(suiteName), tests)
}