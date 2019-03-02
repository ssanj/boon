package boon

package object syntax {

  implicit def toEqSyntax[A](value1: => A): EqSyntax[A] = new EqSyntax[A](value1)

  implicit def toNonEmptySeqOfAssertions(continueSyntax: ContinueSyntax): NonEmptySeq[Assertion] =
    continueSyntax.assertions

  implicit def booleanToDescSyntax(value1: Boolean): DescSyntax[Boolean] =
    new DescSyntax[Boolean]((Defer(() => value1), Defer(() =>true)))

  implicit def toStrRep[T: StringRep](value: T): StringRepSyntax[T] = StringRepSyntax[T](value)

  def failAssertion: DescSyntax[FailedAssertion.type] = FailedAssertion =?= FailedAssertion

  def passAssertion: DescSyntax[PassedAssertion.type] = PassedAssertion =?= PassedAssertion
}