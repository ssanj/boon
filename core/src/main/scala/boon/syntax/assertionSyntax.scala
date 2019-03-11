package boon
package syntax

import Boon.defineAssertion
import Boon.defineAssertionWithContext
import Boon.defineCompositeAssertion

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
  def =?=(value2: => A): DescSyntax[A] = new DescSyntax[A]((defer(value1), defer(value2)), IsEqual)

  def =/=(value2: => A): DescSyntax[A] = new DescSyntax[A]((defer(value1), defer(value2)), IsNotEqual)

  def =!=[T <: Throwable](assertMessage: String => ContinueSyntax)(
    implicit classTag: ClassTag[T], SR: StringRep[A]): ContinueSyntax = {
    val expectedClass = classTag.runtimeClass
    val expectedClassName = expectedClass.getName
    Try(value1).fold[ContinueSyntax](
      e => expectedClass.isAssignableFrom(e.getClass) |# (s"exception class ${expectedClassName}",
                                                           "expected class" -> expectedClassName,
                                                           "got class"      -> e.getClass.getName) and
           assertMessage(e.getMessage),
      s => fail(s"expected ${expectedClassName} but got class:${s.getClass.getName} value:${SR.strRep(s)}") | s"exception class ${expectedClassName}"
    )
  }
}

final class DescSyntax[A](pair: (Defer[A], Defer[A]), equalityType: EqualityType) {
  def |(name: => String)(implicit E: boon.Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertion[A](name, (pair), equalityType)))

  def |#(name: => String, ctx: (String, String)*)(implicit E: boon.Equality[A], D: Difference[A], loc: SourceLocation): ContinueSyntax =
    new ContinueSyntax(NonEmptySeq.nes(defineAssertionWithContext[A](name, (pair), equalityType, Map(ctx:_*))))
}

final case class ContinueSyntax(assertions: NonEmptySeq[Assertion]) {
    def &(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))

    def and(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))

    def seq(name: => String, ctx: (String, String)*)(implicit loc: SourceLocation): ContinueSyntax = ContinueSyntax(
      NonEmptySeq.nes(defineCompositeAssertion(name, assertions, Map(ctx:_*), loc))
    )
}

