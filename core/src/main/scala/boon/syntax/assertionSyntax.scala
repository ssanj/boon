package boon
package syntax

import boon.model.Assertion
import boon.model.Defer
import boon.model.Equality
import boon.model.EqualityType
import boon.model.IsEqual
import boon.model.IsNotEqual
import boon.model.Difference
import boon.model.TestData
import boon.model.Sequential
import boon.model.Independent

import Boon.defineAssertion
import Boon.defineAssertionWithContext

/*
 * Operator Precedence: https://docs.scala-lang.org/tour/operators.html
 *
 * (characters not shown below)
 * * / %
 * + -
 * :
 * = !
 * < >
 * &
 * ^
 * |
 * (all letters)
 */

final class EqSyntax[A](value1: => A) {
  def =?=(value2: => A): Predicate[A] = new Predicate[A]((defer(value1), defer(value2)), IsEqual, noHints)

  def =/=(value2: => A): Predicate[A] = new Predicate[A]((defer(value1), defer(value2)), IsNotEqual, noHints)
}

final class Predicate[A](pair: (Defer[A], Defer[A]), equalityType: EqualityType, hints: Option[NonEmptySeq[String]]) {
  def |(name: => String)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax = {
    val diff = hints.fold(Difference[A])(Difference.fromResult[A](_))
    new ContinueSyntax(NonEmptySeq.nes(defineAssertion[A](name, (pair), equalityType)(implicitly, diff, implicitly)))
  }

  def |#(name: => String, ctx: (String, String)*)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax = {
    val diff = hints.fold(Difference[A])(Difference.fromResult[A](_))
    new ContinueSyntax(
      NonEmptySeq.nes(
        defineAssertionWithContext[A](name, (pair), equalityType, Map(ctx:_*))(implicitly, diff, implicitly)))
  }

  def >>(moreHints: => NonEmptySeq[String]): Predicate[A] =
    new Predicate[A](pair, equalityType, hints.map(_.concat(moreHints)).orElse(Option(moreHints)))
}

final case class ContinueSyntax(assertions: NonEmptySeq[Assertion]) {
    def and(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))

    def seq(): TestData = TestData(assertions, Sequential)

    def ind(): TestData = TestData(assertions, Independent)
}
