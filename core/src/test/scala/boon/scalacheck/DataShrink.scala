package boon.scalacheck

import boon.data.NonEmptySeq
import org.scalacheck.Shrink

trait NaturalTransformation[A[_], B[_]] {
  def from[T](value: A[T]): B[T]
  def to[T](value: B[T]): Option[A[T]]
}

object NaturalTransformation {
  implicit def nonEmptySeqToVector: NaturalTransformation[NonEmptySeq, Vector] = new NaturalTransformation[NonEmptySeq, Vector] {
    def from[T](value: NonEmptySeq[T]): Vector[T] = value.toVector
    def to[T](value: Vector[T]): Option[NonEmptySeq[T]] = NonEmptySeq.fromVector[T](value)
  }
}

object Shrinkable {
  def toShrinkable[A[_], B[_], T](implicit shrinkB: Shrink[B[T]], nat: NaturalTransformation[A, B]): Shrink[A[T]] =
    Shrink[A[T]] { at =>
      shrinkB.shrink(nat.from[T](at)).map(nat.to[T]).collect {
        case Some(as) => as
      }
    }
}

object DataShrink {
  implicit def nonEmptySeqShrink[T]: Shrink[NonEmptySeq[T]] = Shrinkable.toShrinkable[NonEmptySeq, Vector, T]
}