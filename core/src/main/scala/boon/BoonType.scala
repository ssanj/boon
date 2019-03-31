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

      override def diff(a1: A, a2: A): NonEmptySeq[String] = diffy(a1, a2)
    }

  def fromInstances[A](implicit equality: Equality[A], strRep: StringRep[A], diff: Difference[A]): BoonType[A] = {
    BoonType.from[A](equality.eql, strRep.strRep, diff.diff)
  }

  def defaults[A]: BoonType[A] = {
    implicit val sRep = StringRep.genericStringRep[A]
    BoonType.from[A](Equality.genericEquality[A].eql, sRep.strRep, Difference.genericDifference[A].diff)
  }

  def defaultsWithEquality[A](equality: (A, A) => Boolean): BoonType[A] = {
    val defaultBoonType = defaults[A]
    from[A](equality, defaultBoonType.strRep, defaultBoonType.diff)
  }

  def defaultsWithStringRep[A](strRep: A => String): BoonType[A] = {
    val defaultBoonType = defaults[A]
    from[A](defaultBoonType.eql, strRep, defaultBoonType.diff)
  }

  def defaultsWithDiff[A](diff: (A, A) => NonEmptySeq[String]): BoonType[A] = {
    val defaultBoonType = defaults[A]
    from[A](defaultBoonType.eql, defaultBoonType.strRep, diff)
  }

  def contraBoonType[A, B](bToA: B => A)(implicit boonTypeA: BoonType[A]): BoonType[B] = {
    from[B]((b1, b2) => boonTypeA.eql(bToA(b1), bToA(b2)),
             b => boonTypeA.strRep(bToA(b)),
             (b1, b2) => boonTypeA.diff(bToA(b1), bToA(b2))
    )
  }

  implicit def fromListBoonTypeToSeq[A](
    implicit listEquality: Equality[List[A]],
             listStringRep: StringRep[List[A]],
             listDiff: Difference[List[A]]): BoonType[Seq[A]] = {
    contraBoonType[List[A], Seq[A]](_.toList)(fromInstances[List[A]])
  }
}