package boon
package syntax

import Boon.defineAssertion
import Boon.defineAssertionWithContext

import scala.util.Try

final class EqSyntax[A](value1: => A) {
  def =?=(value2: => A): DescSyntax[A] = new DescSyntax[A]((defer(value1), defer(value2)))

  def =/=(value2: => A): DescSyntax[Not[A]] = new DescSyntax[Not[A]]((defer(Not(value1)), defer(Not(value2))))

  def =!=(value2: => BoonEx): DescSyntax[BoonEx] =  {
    val d1 =
      defer(Try(value1).fold[BoonEx](e => Ex(e.getClass.getName, e.getMessage),
                                     s => NotEx(s.getClass.getName)))

    val d2 = defer(value2)

    new DescSyntax[BoonEx]((d1, d2))
  }
}

final class DescSyntax[A](pair: (Defer[A], Defer[A])) {
  def |(name: => String)(implicit E: boon.Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertion[A](name, (pair))))

  def |#(name: => String, ctx: (String, String)*)(implicit E: boon.Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertionWithContext[A](name, (pair), Map(ctx:_*))))
}

final case class ContinueSyntax(assertions: NonEmptySeq[Assertion]) {
    def &(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))

    def and(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))
}

