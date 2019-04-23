package boon
package data

sealed trait These[+A, +B]

object These {
  final case class OnlyLeft[A](value: A) extends These[A, Nothing]
  final case class OnlyRight[B](value: B) extends These[Nothing, B]
  final case class Both[A, B](left: A, right: B) extends These[A, B]

  final case class FoldSyntax[A, B](these: These[A, B]) {
    def fold[C](onlyLeft: A => C, onlyRight: B => C, both: (A, B) => C): C = these match {
      case OnlyLeft(value)   => onlyLeft(value)
      case OnlyRight(value)  => onlyRight(value)
      case Both(left, right) => both(left, right)
    }
  }

  implicit def foldThese[A, B](these: These[A, B]): FoldSyntax[A, B] = FoldSyntax[A, B](these)
}