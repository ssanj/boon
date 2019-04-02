package boon
package model

sealed trait EqualityType
case object IsEqual extends EqualityType
case object IsNotEqual extends EqualityType

object EqualityType {
  final case class FoldSyntax(et: EqualityType) {
    def fold[A](isNotEqual: => A, isEqual: => A): A = et match {
      case IsEqual    => isEqual
      case IsNotEqual => isNotEqual
    }
  }

  implicit def foldEqualityType(et: EqualityType): FoldSyntax = FoldSyntax(et)
}