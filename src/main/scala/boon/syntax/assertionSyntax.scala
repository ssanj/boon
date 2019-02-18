package boon
package syntax

import Boon.defineAssertion

final case class EqSyntax[A](value1: A) {
  def =?=(value2: A): DescSyntax[A] = DescSyntax[A]((value1, value2))

  def =/=(value2: A): DescSyntax[Not[A]] = DescSyntax[Not[A]]((Not(value1), Not(value2)))
}

final case class DescSyntax[A](pair: (A, A)) {
  def |(name: String)(implicit E: boon.Equality[A], D: Difference[A]): ContinueSyntax =
    ContinueSyntax(NonEmptySeq.nes(defineAssertion[A](name, (pair))))
}

final case class ContinueSyntax(assertions: NonEmptySeq[Assertion]) {
    def &(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))
}

