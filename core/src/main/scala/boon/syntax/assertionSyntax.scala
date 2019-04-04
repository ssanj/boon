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
  def =?=(value2: => A): DescSyntax[A] = new DescSyntax[A]((defer(value1), defer(value2)), IsEqual, noHints)

  def =/=(value2: => A): DescSyntax[A] = new DescSyntax[A]((defer(value1), defer(value2)), IsNotEqual, noHints)
}

final class DescSyntax[A](pair: (Defer[A], Defer[A]), equalityType: EqualityType, hints: Seq[String]) {
  def |(name: => String)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertion[A](name, (pair), equalityType, hints)))

  def |#(name: => String, ctx: (String, String)*)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertionWithContext[A](name, (pair), equalityType, Map(ctx:_*), hints)))

  def >>(moreHints: Seq[String]): DescSyntax[A] = new DescSyntax[A](pair, equalityType, hints ++ moreHints)
}

final case class ContinueSyntax(assertions: NonEmptySeq[Assertion]) {
    def and(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))

    def seq(): TestData = TestData(assertions, Sequential)

    def ind(): TestData = TestData(assertions, Independent)
}
