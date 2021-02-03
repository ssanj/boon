package boon
package model

import scala.util.Try
import scala.collection.IterableOnce
import boon.data.NonEmptySeq

trait StringRep[A] {
  def strRep(a: A): String

  trait StringRepLaws {

    /* Stability
     * strRep(a) == strRep(a), over many invocations
     */
    def stability(value: A, equality: Equality[String]): Boolean =
      equality.eql(strRep(value), strRep(value)) &&
      equality.eql(strRep(value), strRep(value)) &&
      equality.eql(strRep(value), strRep(value))

    /* Equal values should have the same String representation
     * x == y, then strRep(x) == strRep(y)
     */
    def equalValuesHaveSameStringRep(value1: A, value2: A, equalityA: Equality[A], equalityStr: Equality[String]): Boolean =
      !equalityA.eql(value1, value2) || equalityStr.eql(strRep(value1), strRep(value2))

    /* Equal Strings should have equal values
     * strRep(x) == strRep(y) then x == y
     */
    def sameStringRepHasEqualValues(value1: A, value2: A, equalityA: Equality[A], equalityStr: Equality[String]): Boolean =
      !equalityStr.eql(strRep(value1), strRep(value2)) || equalityA.eql(value1, value2)
  }

  val stringReplaws = new StringRepLaws {}
}

object StringRep {

  def apply[A: StringRep]: StringRep[A] = implicitly[StringRep[A]]

  def from[A](str: A => String): StringRep[A] = new StringRep[A] {
    override def strRep(value: A): String = str(value)
  }

  def genericStringRep[A]: StringRep[A] = from[A](_.toString)

  implicit val intStringRep     = genericStringRep[Int]
  implicit val longStringRep    = genericStringRep[Long]
  implicit val booleanStringRep = genericStringRep[Boolean]
  implicit val floatStringRep   = genericStringRep[Float]
  implicit val doubleStringRep  = genericStringRep[Double]

  implicit val stringStringRep = from[String](str => s""""$str"""")

  implicit val charStringRep = from[Char](c => s"'$c'")

  private def colStringRep[A: StringRep, F[A] <: IterableOnce[A]](prefix: String, open: String, close: String) =
    from[F[A]](_.iterator.map(StringRep[A].strRep).mkString(s"${prefix}${open}", ", ", s"${close}"))

  implicit def arrayStringRep[A: StringRep]: StringRep[Array[A]] =
    from[Array[A]](ar => ar.mkString("Array[", ",", "]"))

  implicit def listStringRep[A: StringRep] = colStringRep[A, List]("List", "(", ")")

  implicit def vectorStringRep[A: StringRep] = colStringRep[A, Vector]("Vector", "(", ")")

  implicit def setStringRep[A: StringRep] = colStringRep[A, Set]("Set", "(", ")")

  implicit def seqStringRep[A: StringRep] = colStringRep[A, Seq]("Seq", "(", ")")

  implicit def nonEmptySeqStringRep[A: StringRep] = from[NonEmptySeq[A]](_.map(StringRep[A].strRep).mkString("NES(", ",", ")"))

  implicit def eitherStringRep[A: StringRep, B: StringRep] =
    from[Either[A, B]](_.fold(l => s"Left(${StringRep[A].strRep(l)})", r => s"Right(${StringRep[B].strRep(r)})"))

  implicit def throwableStringRep = from[Throwable](t => s"${t.getClass.getName}(${t.getMessage})")

  implicit def tryStringRep[A: StringRep] =
    from[Try[A]](_.fold(t => s"Failure(${StringRep[Throwable].strRep(t)})", success => s"Success(${StringRep[A].strRep(success)})"))

  implicit def optionStringRep[A: StringRep] = from[Option[A]](_.fold("None")(v => s"Some(${StringRep[A].strRep(v)})"))

  implicit def pairStringRep[A: StringRep, B: StringRep] =
    from[(A, B)](pair => s"(${StringRep[A].strRep(pair._1)}, ${StringRep[B].strRep(pair._2)})")

  implicit def tripleStringRep[A: StringRep, B: StringRep, C: StringRep] =
    from[(A, B, C)](triple => s"(${StringRep[A].strRep(triple._1)}, ${StringRep[B].strRep(triple._2)}, ${StringRep[C].strRep(triple._3)})")

  implicit def tuple4StringRep[A: StringRep, B: StringRep, C: StringRep, D: StringRep] =
    from[(A, B, C, D)]{ tuple =>
      "(" +
        s"${StringRep[A].strRep(tuple._1)}, "  +
        s"${StringRep[B].strRep(tuple._2)}, "  +
        s"${StringRep[C].strRep(tuple._3)}, "  +
        s"${StringRep[D].strRep(tuple._4)}"    +
      ")"
    }

  implicit def mapStringRep[A: StringRep, B: StringRep] =
    from[Map[A, B]](_.map { case (k, v) =>  s"${StringRep[A].strRep(k)} -> ${StringRep[B].strRep(v)}" }.mkString("Map(", ",", ")"))
}