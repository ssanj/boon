package boon
package syntax

import boon.model.AssertionData
import boon.model.Predicate
import boon.data.NonEmptySeq

final class PredicateFailSyntax(diff: => NonEmptySeq[String]) extends PredicateSyntaxEx {

  private val predicate: Predicate[Boolean] = false

  override def |(name: => String, ctx: (String, String)*): AssertionData = predicate >> diff | (name, ctx:_*)
}
