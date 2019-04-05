package boon
package model


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
  }

  val stringReplaws = new StringRepLaws {}
}

object StringRep {

  def apply[A: StringRep]: StringRep[A] = implicitly[StringRep[A]]

  def genericStringRep[A]: StringRep[A] = new StringRep[A] {
    override def strRep(a: A): String = a.toString
  }

  implicit val intStringRep     = genericStringRep[Int]
  implicit val longStringRep    = genericStringRep[Long]
  implicit val booleanStringRep = genericStringRep[Boolean]
  implicit val floatStringRep   = genericStringRep[Float]
  implicit val doubleStringRep  = genericStringRep[Double]

  implicit object StringStringRep extends StringRep[String] {
    override def strRep(a: String): String = s""""$a""""
  }

  implicit object CharStringRep extends StringRep[Char] {
    override def strRep(a: Char): String = s"'$a'"
  }

  implicit def listStringRep[A](implicit S: StringRep[A]): StringRep[List[A]] = new StringRep[List[A]] {
    override def strRep(xs: List[A]): String = xs.map(S.strRep).mkString("List[", ",", "]")
  }

  implicit def nonEmptySeqStringRep[A](implicit S: StringRep[A]): StringRep[NonEmptySeq[A]] = new StringRep[NonEmptySeq[A]] {
    override def strRep(xs: NonEmptySeq[A]): String = xs.map(S.strRep).mkString("NES(", ",", ")")
  }

  implicit def eitherStringRep[A, B](implicit LS: StringRep[A], RS: StringRep[B]): StringRep[Either[A, B]] = new StringRep[Either[A, B]] {
    override def strRep(xs: Either[A, B]): String = xs.fold(l => s"Left(${LS.strRep(l)})", r => s"Right(${RS.strRep(r)})")
  }

  implicit def optionStringRep[A](implicit S: StringRep[A]): StringRep[Option[A]] = new StringRep[Option[A]] {
    override def strRep(xs: Option[A]): String = xs.fold("None")(v => s"Some(${S.strRep(v)})")
  }

  implicit def pairStringRep[A, B](implicit SA: StringRep[A], SB: StringRep[B]): StringRep[Tuple2[A, B]] = new StringRep[Tuple2[A, B]] {
    override def strRep(pair: Tuple2[A, B]): String = s"(${SA.strRep(pair._1)}, ${SB.strRep(pair._2)})"
  }

  implicit def mapStringRep[A, B](implicit SA: StringRep[A], SB: StringRep[B]): StringRep[Map[A, B]] = new StringRep[Map[A, B]] {

    override def strRep(map: Map[A, B]): String = map.map { case (k, v) =>  s"${SA.strRep(k)} -> ${SB.strRep(v)}" }.mkString("Map(", ",", ")")
  }
}