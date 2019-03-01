package boon
package syntax

import Boon.defineAssertion
import Boon.defineAssertionWithContext

import scala.util.Try

final class EqSyntax[A](value1: => A) {
  def =?=(value2: => A): DescSyntax[A] = new DescSyntax[A]((Defer(() => value1), Defer(() => value2)))

  def =/=(value2: => A): DescSyntax[Not[A]] = new DescSyntax[Not[A]]((Defer(() => Not(value1)), Defer(() => Not(value2))))

  def =!=(value2: => BoonEx): DescSyntax[BoonEx] =  {
    val d1 =
      Defer(() =>
        Try(value1).fold[BoonEx](e => Ex(e.getClass.getName, e.getMessage),
                                 s => NotEx(s.getClass.getName)))

    val d2 = Defer(() => value2)

    new DescSyntax[BoonEx]((d1, d2))
  }

  def =!!=(handle: BoonEx => ContinueSyntax): ContinueSyntax = {
    val ex =
      Try(value1).fold[BoonEx](e => Ex(e.getClass.getName, e.getMessage),
                               s => NotEx(s.getClass.getName))

    handle(ex)
  }

  def =!!!=(assertClassName: String => DescSyntax[Boolean], assertMessage: String => DescSyntax[Boolean]): ContinueSyntax = {
    val ex =
      Try(value1).fold[BoonEx](e => Ex(e.getClass.getName, e.getMessage),
                               s => NotEx(s.getClass.getName))

    ex match {
      case Ex(cn, msg) =>
        (assertClassName(cn) | "Exception class name") &
        (assertMessage(msg) | "Exception message")
      case NotEx(cn) => failAssertion | s"Not an Exception: $cn"
    }
  }
}

final class DescSyntax[A](pair: (Defer[A], Defer[A])) {
  def |(name: => String)(implicit E: boon.Equality[A], D: Difference[A]): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertion[A](name, (pair))))

  def |#(name: => String, ctx: (String, String)*)(implicit E: boon.Equality[A], D: Difference[A]): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertionWithContext[A](name, (pair), Map(ctx:_*))))
}

final case class ContinueSyntax(assertions: NonEmptySeq[Assertion]) {
    def &(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))
}

