package boon
package syntax

import boon.model.AssertionData

object either {

  final class EitherSyntax[A](value: => A) {
    def left[B]: Either[A, B] = Left[A, B](value)

    def right[B]: Either[B, A] = Right[B, A](value)
  }

  implicit def toEitherSyntax[A](value: => A): EitherSyntax[A] = new EitherSyntax[A](value)

  def left_?[A, B](either: Either[A, B])(f: A => AssertionData): AssertionData = {
    either.fold(f, _ => fail(s"expected Left but got ${either}") | "expected Left" )
  }

  def right_?[A, B](either: Either[A, B])(f: B => AssertionData): AssertionData = {
    either.fold(_ => fail(s"expected Right but got ${either}") | "expected Right", f)
  }
}

