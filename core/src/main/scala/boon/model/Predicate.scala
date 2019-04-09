package boon
package model

import Boon.defineAssertionWithContext

final class Predicate[A](val pair: (Defer[A], Defer[A]), val equalityType: EqualityType, val overrideErrors: Option[NonEmptySeq[String]]) {

  def |(name: => String, ctx: (String, String)*)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): AssertionData = {
    val diff = overrideErrors.fold(Difference[A])(Difference.fromResult[A](_))
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertionWithContext[A](name, (pair), equalityType, Map(ctx:_*))(implicitly, diff, implicitly)))
  }

  def >>(moreOverrides: => NonEmptySeq[String]): Predicate[A] =
    new Predicate[A](pair, equalityType, overrideErrors.map(_.concat(moreOverrides)).orElse(Option(moreOverrides)))
}

