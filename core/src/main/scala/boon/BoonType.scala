package boon

import boon.model.Difference
import boon.model.Equality
import boon.model.StringRep

trait BoonType[A] extends Equality[A] with StringRep[A] with Difference[A]

object BoonType {

  def apply[T: BoonType]: BoonType[T] = implicitly[BoonType[T]]

  def from[A](equality: (A, A) => Boolean, stringy: A => String, diffy: (A, A) => NonEmptySeq[String]): BoonType[A] =
    new BoonType[A] {

      override def eql(a1: A, a2: A): Boolean = equality(a1, a2)

      override def strRep(a: A): String = stringy(a)

      def diff(a1: A, a2: A): NonEmptySeq[String] = diffy(a1, a2)
    }

  def defaults[A]: BoonType[A] = {
    implicit val sRep = StringRep.genericStringRep[A]
    BoonType.from[A](Equality.genericEquality[A].eql, sRep.strRep, Difference.genericDifference[A].diff)
  }
}