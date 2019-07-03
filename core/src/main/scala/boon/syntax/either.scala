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

  def left_?[A: StringRep, B: StringRep](either: Either[A, B])(f: A => AssertionData)(implicit loc: SourceLocation): AssertionData = {
    either.fold(f, _ => invalid(s"expected Left but got ${either}") | "expected Left" ).
      context(Map("value" -> either.strRep))
  }

  def isLeft[A: StringRep, B: StringRep](either: Either[A, B])(implicit loc: SourceLocation): AssertionData = {
    either.isLeft >> (one(s"expected Left got: ${either.strRep}"), Replace) | ("is Left", "value" -> either.strRep)
  }

  def right_?[A: StringRep, B: StringRep](either: Either[A, B])(f: B => AssertionData)(implicit loc: SourceLocation): AssertionData = {
    either.fold(_ => invalid(s"expected Right but got ${either.strRep}") | "expected Right", f).
      context(Map("value" -> either.strRep))
  }

  def isRight[A: StringRep, B: StringRep](either: Either[A, B])(implicit loc: SourceLocation): AssertionData = {
    either.isRight >> (one(s"expected Right got: ${either.strRep}"), Replace) | ("is Right", "value" -> either.strRep)
  }
}

