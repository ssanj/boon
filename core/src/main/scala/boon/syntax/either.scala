package boon
package syntax

import boon.model.StringRep
import boon.model.AssertionData

object either {

  final class EitherSyntax[A](value: => A) {
    def left[B]: Either[A, B] = Left[A, B](value)

    def right[B]: Either[B, A] = Right[B, A](value)
  }

  implicit def toEitherSyntax[A](value: => A): EitherSyntax[A] = new EitherSyntax[A](value)

  def left_?[A: StringRep, B: StringRep](either: Either[A, B])(f: A => AssertionData): AssertionData = {
    either.fold[AssertionData](f, _ => invalid(errorTemplate(plain("Left"), either)) | "expected Left").
      context(Map("value" -> either.strRep))
  }

  def isLeft[A: StringRep, B: StringRep](either: Either[A, B]): AssertionData = {
    either.isLeft.>> (one(errorTemplate(plain("Left"), either)))(Replace) || ("is Left") |> (one("value" -> either.strRep))
  }

  def right_?[A: StringRep, B: StringRep](either: Either[A, B])(f: B => AssertionData): AssertionData = {
    either.fold[AssertionData](_ => invalid(errorTemplate(plain("Right"), either)) | "expected Right", f).
      ctx("value" -> either.strRep)
  }

  def isRight[A: StringRep, B: StringRep](either: Either[A, B]): AssertionData = {
    either.isRight.>> (one(errorTemplate(plain("Right"), either)))(Replace) || "is Right" |> one("value" -> either.strRep)
  }
}

