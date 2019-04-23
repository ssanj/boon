package boon

import boon.model._
import boon.data.NonEmptySeq

abstract class SuiteLike(val suiteName: String) {

  def tests: NonEmptySeq[Test]

  def suite: DeferredSuite = DeferredSuite(SuiteName(suiteName), tests)
}