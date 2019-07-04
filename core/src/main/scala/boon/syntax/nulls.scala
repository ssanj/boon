package boon
package syntax

import boon.model.StringRep
import boon.model.Null
import boon.model.AssertionData

object nulls {
  def null_![A: StringRep](value: => A)(f : A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(invalid(errorTemplate(plain("not null"), Null)) | ("expected not null", "value" -> "null"))(v => f(v).context(ctxM(v)))

  def isNotNull[A: StringRep](value: => A)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(
      invalid(errorTemplate(plain("not null"), Null)) | ("is not null", "value" -> "null"))(_ => pass | ("is not null", ctx(value)))

  def null_?[A: StringRep](value: => A)(f : => AssertionData)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(f.context(Map("value" -> "null")))(v => invalid(errorTemplate(plain("null"), v)) | ("expected null", ctx(v)))

  def isNull[A: StringRep](value: => A)(implicit loc: SourceLocation): AssertionData =
    fold[A, AssertionData](value)(
      pass | ("is null", "value" -> "null"))(
      _ => invalid(errorTemplate(plain("null"), value)) | ("is null", "value" -> value.strRep))

  private def fold[A, B](value: => A)(n: => B)(s: A => B): B = Option(value).fold(n)(s)
}