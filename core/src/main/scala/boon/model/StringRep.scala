package boon
package model


trait StringRep[A] {
  def strRep(a: A): String
}

object StringRep {

  def apply[A: StringRep]: StringRep[A] = implicitly[StringRep[A]]

  def genericStringRep[A]: StringRep[A] = new StringRep[A] {
    override def strRep(a: A): String = a.toString
  }

  implicit object IntStringRep extends StringRep[Int] {
    override def strRep(a: Int): String = genericStringRep[Int].strRep(a)
  }

  implicit object LongStringRep extends StringRep[Long] {
    override def strRep(a: Long): String = genericStringRep[Long].strRep(a)
  }

  implicit object FloatStringRep extends StringRep[Float] {
    override def strRep(a: Float): String = genericStringRep[Float].strRep(a)
  }

  implicit object DoubleStringRep extends StringRep[Double] {
    override def strRep(a: Double): String = genericStringRep[Double].strRep(a)
  }

  implicit object BooleanStringRep extends StringRep[Boolean] {
    override def strRep(a: Boolean): String = genericStringRep[Boolean].strRep(a)
  }

  implicit object StringStringRep extends StringRep[String] {
    override def strRep(a: String): String = s""""$a""""
  }

  implicit object CharStringRep extends StringRep[Char] {
    override def strRep(a: Char): String = s"'$a'"
  }

  implicit def listStringRep[A](implicit S: StringRep[A]): StringRep[List[A]] = new StringRep[List[A]] {
    override def strRep(xs: List[A]): String = xs.map(S.strRep).mkString("[", ",", "]")
  }

  implicit def optionStringRep[A](implicit S: StringRep[A]): StringRep[Option[A]] = new StringRep[Option[A]] {
    override def strRep(xs: Option[A]): String = xs.fold("None")(v => s"Some(${S.strRep(v)})")
  }

  implicit def eitherStringRep[A, B](implicit LS: StringRep[A], RS: StringRep[B]): StringRep[Either[A, B]] = new StringRep[Either[A, B]] {
    override def strRep(xs: Either[A, B]): String = xs.fold(l => s"Left(${LS.strRep(l)})", r => s"Right(${RS.strRep(r)})")
  }

  implicit def pairStringRep[A, B](implicit SA: StringRep[A], SB: StringRep[B]): StringRep[Tuple2[A, B]] = new StringRep[Tuple2[A, B]] {
    override def strRep(pair: Tuple2[A, B]): String = s"(${SA.strRep(pair._1)}, ${SB.strRep(pair._2)})"
  }

  implicit def mapStringRep[A, B](implicit SA: StringRep[A], SB: StringRep[B]): StringRep[Map[A, B]] = new StringRep[Map[A, B]] {

    override def strRep(map: Map[A, B]): String = map.map { case (k, v) =>  s"${SA.strRep(k)} -> ${SB.strRep(v)}" }.mkString("Map(", ",", ")")
  }

  implicit object failableAssertionStringRep extends StringRep[FailableAssertion] {
    override def strRep(fa: FailableAssertion): String = fa match {
      case FailedAssertion(reason) => s"Failed(reason=${reason})"
      case PassedAssertion         => s"Passed"
    }
  }
}