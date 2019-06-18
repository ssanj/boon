package boon
package syntax

import boon.model.StringRep
import boon.model.AssertionData

object nulls {
  def null_![A](value: => A)(f : A => AssertionData): AssertionData =
    fold[A, AssertionData](value)(fail(s"expected not null value") | "not null value")(f)

  def isNotNull[A](value: => A)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(
      invalid(s"expected not null got: null") | "not null")(
      _ => pass | "not null")

  def null_?[A](value: => A)(f : => AssertionData): AssertionData =
    fold[A, AssertionData](value)(f)(v => fail(s"expected null but got: $v") | "null value")

  def isNull[A: StringRep](value: => A)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(
      pass | "is null")(
      _ => invalid(s"expected null got: ${StringRep[A].strRep(value)}") | "is null")

  private def fold[A, B](value: => A)(n: => B)(s: A => B): B = Option(value).fold(n)(s)
}