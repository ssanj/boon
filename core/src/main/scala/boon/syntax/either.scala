package boon.syntax

object either {

  final class EitherSyntax[A](value: => A) {
    def l[B]: Either[A, B] = Left[A, B](value)

    def r[B]: Either[B, A] = Right[B, A](value)

    def ->[B]: Either[B, A] = Right[B, A](value)
  }

  implicit def toEitherSyntax[A](value: => A): EitherSyntax[A] = new EitherSyntax[A](value)

  def l_?[A, B](f: A => ContinueSyntax)(either: Either[A, B]): ContinueSyntax = {
    either.fold(f, _ => fail(s"expected Left but got ${either}") | "expected Left" )
  }

  def r_?[A, B](f: B => ContinueSyntax)(either: Either[A, B]): ContinueSyntax = {
    either.fold(t_ => fail(s"expected Right but got ${either}") | "expected Right", f)
  }

}

