package boon

sealed trait These[+A, +B]
final case class OnlyLeft[A](value: A) extends These[A, Nothing]
final case class OnlyRight[B](value: B) extends These[Nothing, B]
final case class Both[A, B](left: A, right: B) extends These[A, B]