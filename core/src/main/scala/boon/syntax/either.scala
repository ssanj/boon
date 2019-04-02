package boon.syntax

object either {

  final class EitherSyntax[A](value: => A) {
    def left[B]: Either[A, B] = Left[A, B](value)

    def right[B]: Either[B, A] = Right[B, A](value)
  }

  implicit def toEitherSyntax[A](value: => A): EitherSyntax[A] = new EitherSyntax[A](value)

  def left_?[A, B](f: A => ContinueSyntax)(either: Either[A, B]): ContinueSyntax = {
    either.fold(f, _ => fail(s"expected Left but got ${either}") | "expected Left" )
  }

  def right_?[A, B](f: B => ContinueSyntax)(either: Either[A, B]): ContinueSyntax = {
    either.fold(t_ => fail(s"expected Right but got ${either}") | "expected Right", f)
  }

}

