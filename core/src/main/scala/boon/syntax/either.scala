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
    either.fold(f, _ => fail(s"expected Left but got ${either}") | "expected Left" ).
      context(Map("either" -> StringRep[Either[A, B]].strRep(either)))
  }

  def isLeft[A: StringRep, B: StringRep](either: Either[A, B])(implicit loc: SourceLocation): AssertionData = {
    either.isLeft >> (one(s"expected Left got: ${StringRep[Either[A, B]].strRep(either)}"), Replace) | "is Left"
  }

  def right_?[A: StringRep, B: StringRep](either: Either[A, B])(f: B => AssertionData): AssertionData = {
    either.fold(_ => fail(s"expected Right but got ${either}") | "expected Right", f).
      context(Map("either" -> StringRep[Either[A, B]].strRep(either)))
  }

  def isRight[A: StringRep, B: StringRep](either: Either[A, B])(implicit loc: SourceLocation): AssertionData = {
    either.isRight >> (one(s"expected Right got: ${StringRep[Either[A, B]].strRep(either)}"), Replace) | "is Right"
  }
}

