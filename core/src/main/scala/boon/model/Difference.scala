package boon
package model

import NonEmptySeq.nes

//This is not a Typeclass.
trait Difference[A] {
  def diff(a1: A, a2: A): NonEmptySeq[String]
}

object Difference {

  def apply[A: Difference]: Difference[A] = implicitly[Difference[A]]

  def genericDifference[A](implicit rep: StringRep[A]): Difference[A] = new Difference[A] {
    override def diff(a1: A, a2: A): NonEmptySeq[String] = one(s"${rep.strRep(a1)} != ${rep.strRep(a2)}")
  }

  def from[A](f: (A, A) => NonEmptySeq[String]): Difference[A] = new Difference[A] {
    def diff(a1: A, a2: A): NonEmptySeq[String] = f(a1, a2)
  }

  def fromResult[A](f: => NonEmptySeq[String]): Difference[A] = new Difference[A] {
    def diff(a1: A, a2: A): NonEmptySeq[String] = f
  }

  implicit val intDifference     = genericDifference[Int]
  implicit val longDifference    = genericDifference[Long]
  implicit val floatDifference   = genericDifference[Float]
  implicit val doubleDifference  = genericDifference[Double]
  implicit val booleanDifference = genericDifference[Boolean]
  implicit val stringDifference  = genericDifference[String]
  implicit val charDifference    = genericDifference[Char]

  private def seqDiff[A](colL: Seq[A], colR: Seq[A])(summary: String): NonEmptySeq[String] = {

    def contents(col: Seq[A]): String = if (col.isEmpty) "-" else col.mkString("[", ",", "]")

    val both    = colL.filter(colR.contains(_))
    val left    = colL.filter(!colR.contains(_))
    val right   = colR.filter(!colL.contains(_))

    val bothString  = contents(both)
    val leftString  = contents(left)
    val rightString = contents(right)

    nes(
      s"${summary}",
      s"both: ${bothString}",
      s"only on left: ${leftString}",
      s"only on right: ${rightString}"
    )
  }

  implicit def listDifference[A: StringRep]: Difference[List[A]] = new Difference[List[A]] {
    val rep = StringRep[List[A]]
    override def diff(xs: List[A], ys: List[A]): NonEmptySeq[String] = {
      val summary = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
      seqDiff[A](xs, ys)(summary)
    }
  }

  implicit def nonEmptySeqDifference[A: StringRep]: Difference[NonEmptySeq[A]] = new Difference[NonEmptySeq[A]] {
    val rep = StringRep[NonEmptySeq[A]]
    override def diff(xs: NonEmptySeq[A], ys: NonEmptySeq[A]): NonEmptySeq[String] = {
      val summary = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
      seqDiff[A](xs.toSeq, ys.toSeq)(summary)
    }
  }

  implicit def optionDifference[A: StringRep]: Difference[Option[A]] = new Difference[Option[A]] {
    val rep = StringRep[Option[A]]
    override def diff(xs: Option[A], ys: Option[A]): NonEmptySeq[String] = one(s"${rep.strRep(xs)} != ${rep.strRep(ys)}")
  }

  implicit def eitherDifference[A: StringRep, B: StringRep]: Difference[Either[A, B]] = new Difference[Either[A, B]] {
    val rep = StringRep[Either[A, B]]
    override def diff(xs: Either[A, B], ys: Either[A, B]): NonEmptySeq[String] = one(s"${rep.strRep(xs)} != ${rep.strRep(ys)}")
  }

  implicit def pairDifference[A: StringRep, B: StringRep]: Difference[Tuple2[A, B]] = new Difference[Tuple2[A, B]] {
    val rep = StringRep[Tuple2[A, B]]
    override def diff(pair1: Tuple2[A, B], pair2: Tuple2[A, B]): NonEmptySeq[String] = one(s"${rep.strRep(pair1)} != ${rep.strRep(pair2)}")
  }

  implicit def mapDifference[A: StringRep, B: StringRep]: Difference[Map[A, B]] = new Difference[Map[A, B]] {
    val rep = StringRep[Map[A, B]]
    override def diff(map1: Map[A, B], map2: Map[A, B]): NonEmptySeq[String] = one(s"${rep.strRep(map1)} != ${rep.strRep(map2)}")
  }
}