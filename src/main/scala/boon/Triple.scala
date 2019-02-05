package boon

sealed trait Triple[+A, +B, +C]

object Triple {
  final case class LeftOnly[A](value: A) extends Triple[A, Nothing, Nothing]
  final case class RightOnly[C](value: C) extends Triple[Nothing, Nothing, C]
  final case class Middle[B](value: B) extends Triple[Nothing, B, Nothing]
}
