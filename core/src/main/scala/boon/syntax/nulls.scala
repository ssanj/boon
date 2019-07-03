package boon
package syntax

import boon.model.StringRep
import boon.model.AssertionData

object nulls {
  def null_![A: StringRep](value: => A)(f : A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(invalid(s"expected not null value") | ("expected not null", "value" -> "null"))(v => f(v).context(Map("value" -> v.strRep)))

  def isNotNull[A](value: => A)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(
      invalid(s"expected not null got: null") | "is not null")(
      _ => pass | "not null")

  def null_?[A: StringRep](value: => A)(f : => AssertionData)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(f.context(Map("value" -> "null")))(v => invalid(s"expected null got: ${v.strRep}") | ("expected null", "value" -> v.strRep))

  def isNull[A: StringRep](value: => A)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(
      pass | "is null")(
      _ => invalid(s"expected null got: ${StringRep[A].strRep(value)}") | "is null")

  private def fold[A, B](value: => A)(n: => B)(s: A => B): B = Option(value).fold(n)(s)
}