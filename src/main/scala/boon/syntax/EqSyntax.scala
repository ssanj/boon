package boon
package syntax

import Boon.defineAssertion

final case class EqSyntax[A](value1: A) {
  def =?=(value2: A): DescSyntax[A] = DescSyntax[A]((value1, value2))
}

final case class DescSyntax[A](pair: (A, A)) {
  def |(name: String)(implicit E: boon.Equality[A], D: Difference[A]): ContinueSyntax =
    ContinueSyntax(NonEmptySeq.nes(defineAssertion[A](name, (pair))))
}

final case class ContinueSyntax(assertions: NonEmptySeq[Assertion]) {
  def &(other: ContinueSyntax): ContinueSyntax = ContinueSyntax(assertions.concat(other.assertions))
}

object EqSyntax {
  implicit def toEqSyntax[A](value1: A): EqSyntax[A] = EqSyntax[A](value1)

  // implicit def toContinueSyntax(assertion: Assertion): ContinueSyntax = ContinueSyntax(NonEmptySeq.nes(assertion))

  implicit def toNonEmptySeqOfAssertions(continueSyntax: ContinueSyntax): NonEmptySeq[Assertion] =
    continueSyntax.assertions
}