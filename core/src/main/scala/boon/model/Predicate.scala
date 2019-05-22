package boon
package model

import boon.syntax.PredicateSyntaxEx
import boon.data.NonEmptySeq
import Boon.defineAssertion

final class Predicate[A](val pair: (Defer[A], Defer[A]), val equalityType: EqualityType) {

  def |(name: => String, ctx: (String, String)*)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): AssertionData = {
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, (pair), equalityType, Map(ctx:_*))
      )
    )
  }

  def |?(name: => String, difference: Difference[A], E: Equality[A], ctx: Map[String, String])(implicit loc: SourceLocation): AssertionData = {
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, (pair), equalityType, ctx)(E, difference, loc)))
  }

  def >>(diff: => NonEmptySeq[String])(implicit E: Equality[A], loc: SourceLocation): PredicateSyntaxEx = new PredicateSyntaxEx {
    override def |(name: => String, ctx: (String, String)*): AssertionData = {
      val difference = Difference.fromResult[A](diff)
      Predicate.this.|?(name, difference, E, Map(ctx:_*))
    }
  }
}
