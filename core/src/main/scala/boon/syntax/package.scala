package boon

package object syntax {

  implicit def aToEqSyntax[A](value1: => A): EqSyntax[A] = new EqSyntax[A](value1)

  implicit def deferAToEqSyntax[A](dValue: Defer[A]): EqSyntax[A] =
    new EqSyntax[A](dValue.run) //this is safe because EqSyntax is lazy

  implicit def toNonEmptySeqOfAssertions(continueSyntax: ContinueSyntax): NonEmptySeq[Assertion] =
    continueSyntax.assertions

  implicit def booleanToDescSyntax(value1: => Boolean): DescSyntax[Boolean] =
    new DescSyntax[Boolean]((defer(value1), defer(true)))

  implicit def toStrRep[T: StringRep](value: T): StringRepSyntax[T] = StringRepSyntax[T](value)

  def fail(reason: String): DescSyntax[FailableAssertion] =
    (FailedAssertion(reason): FailableAssertion) =?= (NotFailedAssertion: FailableAssertion)

  def passAssertion: DescSyntax[PassedAssertion.type] = PassedAssertion =?= PassedAssertion
}