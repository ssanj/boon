package boon
package model

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

  def |?(name: => String, difference: Difference[A], equality: Equality[A], ctx: Map[String, String])(implicit loc: SourceLocation): AssertionData = {
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, (pair), equalityType, ctx)(equality, difference, loc)))
  }

  def >>(diffContent: => NonEmptySeq[String], mod: DifferenceMod)(implicit E: Equality[A], D: Difference[A]): PredicateSyntax =
    >** { case (equality, difference) =>
        (equality,
          mod match {
            case Replace => Difference.fromResult[A](diffContent)
            case Append  => Difference.appendResult(difference, diffContent)
          }
        )
    }

  private def >**(modify: (Equality[A], Difference[A]) => (Equality[A], Difference[A]))(implicit E: Equality[A], D: Difference[A]): PredicateSyntax = new PredicateSyntax {
    override def |(name: => String, ctx: (String, String)*)(implicit loc: SourceLocation): AssertionData = {
      val (equality, difference) = modify(E, D)
      Predicate.this.|(name, ctx:_*)(equality, difference, loc)
    }
  }
}
