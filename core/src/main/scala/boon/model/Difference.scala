package boon
package model

import boon.data.NonEmptySeq

//This is not a Typeclass although it is supplied by the same mechanisms.
trait Difference[A] {
  def diff(a1: A, a2: A, equality: EqualityType): NonEmptySeq[String]
}

object Difference {

  def apply[A: Difference]: Difference[A] = implicitly[Difference[A]]

  def from[A](f: (A, A, EqualityType) => NonEmptySeq[String]): Difference[A] = new Difference[A] {
    def diff(a1: A, a2: A, et: EqualityType): NonEmptySeq[String] = f(a1, a2, et)
  }

  def genericDifference[A](implicit S: StringRep[A]) = from[A]((a1, a2, et) => {
    val symbol = diffSymbol(et)

    one(s"${S.strRep(a1)} $symbol ${S.strRep(a2)}")
  })

  def diffSymbol(et: EqualityType): String = et match {
      case IsEqual    => "!="
      case IsNotEqual => "=="
    }

  def fromResult[A](f: => NonEmptySeq[String]) = from[A]((_, _, _) => f)

  def appendResult[A](difference: Difference[A], f: => NonEmptySeq[String]): Difference[A] =
    from[A]((v1, v2, et) => difference.diff(v1, v2, et).concat(f))

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
  implicit def nonEmptyDifference[A: StringRep]             = colDifference[A, NonEmptySeq](_.toSeq)
  implicit def listDifference[A: StringRep]                 = colDifference[A, List](identity _)
  implicit def setDifference[A: StringRep]                  = colDifference[A, Set](_.toSeq)
  implicit def vectorDifference[A: StringRep]               = colDifference[A, Vector](_.toSeq)
  implicit def seqDifference[A: StringRep]                  = colDifference[A, Seq](identity _)
  implicit def mapDifference[A: StringRep, B: StringRep]    = mapLikeDifference[A, B, Map](_.toSeq)

  implicit def arrayDifference[A](implicit S: StringRep[A]) = colDifference[A, Array](_.toSeq)


  //Tuple
  implicit def pairDifference[A: StringRep, B: StringRep]                               = genericDifference[(A, B)]
  implicit def tripleDifference[A: StringRep, B: StringRep, C: StringRep]               = genericDifference[(A, B, C)]
  implicit def tuple4Difference[A: StringRep, B: StringRep, C: StringRep, D: StringRep] = genericDifference[(A, B, C, D)]

  def colDifference[A: StringRep, S[_]](f: S[A] => Seq[A])(implicit SR: StringRep[S[A]]): Difference[S[A]] =
    from[S[A]]{ (xs: S[A], ys: S[A], et) =>
      val rep = SR
      val symbol = diffSymbol(et)
      val summary = s"${rep.strRep(xs)} $symbol ${rep.strRep(ys)}"
      seqDiff(f(xs), f(ys))(summary)
    }

  def mapLikeDifference[A: StringRep, B: StringRep, S[_, _]](f: S[A, B] => Seq[(A, B)])(implicit SR: StringRep[S[A, B]]) =
    from[S[A, B]]{ (xs: S[A, B], ys: S[A, B], et) =>
      val rep = SR
      val symbol = diffSymbol(et)
      val summary = s"${rep.strRep(xs)} $symbol ${rep.strRep(ys)}"
      implicit val mapElementStringRep: StringRep[(A, B)] = StringRep.from[(A, B)](pair => s"${StringRep[A].strRep(pair._1)} -> ${StringRep[B].strRep(pair._2)}")
      seqDiff[(A, B)](f(xs), f(ys))(summary)
    }

  private def seqDiff[A: StringRep](colL: Seq[A], colR: Seq[A])(summary: String): NonEmptySeq[String] = {

    def contents(col: Seq[A]): String = if (col.isEmpty) "-" else col.map(StringRep[A].strRep).mkString("[", ",", "]")

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