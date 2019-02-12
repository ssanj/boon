package boon

package object syntax {

  implicit def toEqSyntax[A](value1: A): EqSyntax[A] = EqSyntax[A](value1)

  implicit def toNonEmptySeqOfAssertions(continueSyntax: ContinueSyntax): NonEmptySeq[Assertion] =
    continueSyntax.assertions
}