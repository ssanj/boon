package boon
package model

import Boon.defineAssertionWithContext

final class Predicate[A](val pair: (Defer[A], Defer[A]), val equalityType: EqualityType, val hints: Option[NonEmptySeq[String]]) {

  def |(name: => String, ctx: (String, String)*)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): AssertionData = {
    val diff = hints.fold(Difference[A])(Difference.fromResult[A](_))
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertionWithContext[A](name, (pair), equalityType, Map(ctx:_*))(implicitly, diff, implicitly)))
  }

  def >>(moreHints: => NonEmptySeq[String]): Predicate[A] =
    new Predicate[A](pair, equalityType, hints.map(_.concat(moreHints)).orElse(Option(moreHints)))
}

