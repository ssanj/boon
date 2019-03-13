package boon

import boon.model._

package object syntax {

  implicit def aToEqSyntax[A](value1: => A): EqSyntax[A] = new EqSyntax[A](value1)

  implicit def deferAToEqSyntax[A](dValue: Defer[A]): EqSyntax[A] =
    new EqSyntax[A](dValue.run) //this is safe because EqSyntax is lazy

  implicit def toNonEmptySeqOfAssertions(continueSyntax: ContinueSyntax): NonEmptySeq[Assertion] =
    continueSyntax.assertions

  implicit def toTestData(continueSyntax: ContinueSyntax): TestData =
    TestData(continueSyntax.assertions, Independent)

  implicit def booleanToDescSyntax(value1: => Boolean): DescSyntax[Boolean] =
    new DescSyntax[Boolean]((defer(value1), defer(true)), IsEqual)

  implicit def deferBooleanToDescSyntax(value: Defer[Boolean]): DescSyntax[Boolean] =
    new DescSyntax[Boolean]((value, defer(true)), IsEqual)

  implicit def toStrRep[T: StringRep](value: T): StringRepSyntax[T] = StringRepSyntax[T](value)

  def fail(reason: String): DescSyntax[FailableAssertion] =
    upcast[FailedAssertion, FailableAssertion](FailedAssertion(reason)) =?= upcast[PassedAssertion.type, FailableAssertion](PassedAssertion)

  def pass: DescSyntax[FailableAssertion] =
    upcast[PassedAssertion.type, FailableAssertion](PassedAssertion) =?= upcast[PassedAssertion.type, FailableAssertion](PassedAssertion)

  def upcast[Sub, Super](value: Sub)(implicit CAST:Sub <:< Super): Super = CAST(value)
}