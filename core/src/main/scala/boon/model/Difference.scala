package boon
package model

import scala.language.higherKinds

//This is not a Typeclass.
trait Difference[A] {
  def diff(a1: A, a2: A): NonEmptySeq[String]
}

object Difference {

  def apply[A: Difference]: Difference[A] = implicitly[Difference[A]]

  def from[A](f: (A, A) => NonEmptySeq[String]): Difference[A] = new Difference[A] {
    def diff(a1: A, a2: A): NonEmptySeq[String] = f(a1, a2)
  }

  def genericDifference[A](implicit S: StringRep[A]) = from[A]((a1, a2) => one(s"${S.strRep(a1)} != ${S.strRep(a2)}"))

  def fromResult[A](f: => NonEmptySeq[String]) = from[A]((_, _) => f)

  //Primitives
  implicit val intDifference     = genericDifference[Int]
  implicit val longDifference    = genericDifference[Long]
  implicit val floatDifference   = genericDifference[Float]
  implicit val doubleDifference  = genericDifference[Double]
  implicit val booleanDifference = genericDifference[Boolean]
  implicit val stringDifference  = genericDifference[String]
  implicit val charDifference    = genericDifference[Char]

  //Type constructors
  implicit def optionDifference[A: StringRep]               = genericDifference[Option[A]]
  implicit def eitherDifference[A: StringRep, B: StringRep] = genericDifference[Either[A, B]]
  implicit def pairDifference[A: StringRep, B: StringRep]   = genericDifference[Tuple2[A, B]]
  implicit def mapDifference[A: StringRep, B: StringRep]    = genericDifference[Map[A, B]]
  implicit def nonEmptyDifference[A: StringRep]             = seqDifference[A, NonEmptySeq](_.toSeq)
  implicit def listDifference[A: StringRep]                 = seqDifference[A, List](identity _)

  def seqDifference[A: StringRep, S[_]](f: S[A] => Seq[A])(implicit SR: StringRep[S[A]]) =
    from[S[A]]{ (xs: S[A], ys: S[A]) =>
      val rep = SR
      val summary = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
      seqDiff[A](f(xs), f(ys))(summary)
    }

  private def seqDiff[A](colL: Seq[A], colR: Seq[A])(summary: String): NonEmptySeq[String] = {

    def contents(col: Seq[A]): String = if (col.isEmpty) "-" else col.mkString("[", ",", "]")

    val both    = colL.filter(colR.contains(_))
    val left    = colL.filter(!colR.contains(_))
    val right   = colR.filter(!colL.contains(_))

    val bothString  = contents(both)
    val leftString  = contents(left)
    val rightString = contents(right)

    oneOrMore(
      s"${summary}",
      s"both: ${bothString}",
      s"only on left: ${leftString}",
      s"only on right: ${rightString}"
    )
  }
}