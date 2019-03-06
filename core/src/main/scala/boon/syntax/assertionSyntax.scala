package boon
package syntax

import Boon.defineAssertion
import Boon.defineAssertionWithContext

import scala.util.Try
import scala.reflect.ClassTag

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
  def =?=(value2: => A): DescSyntax[A] = new DescSyntax[A]((defer(value1), defer(value2)))

  def =/=(value2: => A): DescSyntax[Not[A]] = new DescSyntax[Not[A]]((defer(Not(value1)), defer(Not(value2))))

  def =!=(value2: => BoonEx): DescSyntax[BoonEx] =  {
    val d1 =
      defer(Try(value1).fold[BoonEx](e => Ex(e.getClass.getName, e.getMessage),
                                     s => NotEx(s.getClass.getName)))

    val d2 = defer(value2)

    new DescSyntax[BoonEx]((d1, d2))
  }

  def =!!=(f: Bex => ContinueSyntax): ContinueSyntax = {
    val ex =
      Try(value1).fold[BoonEx](
        e => Ex(e.getClass.getName, e.getMessage),
        s => NotEx(s.getClass.getName)
      )

    ex match {
      case Ex(cn, msg) => f(Bex(cn, msg))
      case NotEx(cn) => fail(s"expected Exception but got: $cn") | "expect Exception"
    }
  }

  def =!!!=[T <: Throwable](assertMessage: String => ContinueSyntax)(
    implicit classTag: ClassTag[T], SR: StringRep[A]): ContinueSyntax = {
    val expectedClass = classTag.runtimeClass
    Try(value1).fold[ContinueSyntax](
      e => expectedClass.isAssignableFrom(e.getClass) |# (s"exception class ${expectedClass.getName}",
                                                          "expected class" -> expectedClass.getName,
                                                          "got class" -> e.getClass.getName) and
           assertMessage(e.getMessage),
      s => fail(s"expected Exception but got class:${s.getClass.getName} value:${SR.strRep(s)}") | "exception class"
    )
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

