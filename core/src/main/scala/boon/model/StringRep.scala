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

  implicit def listStringRep[A: StringRep] = from[List[A]](_.map(StringRep[A].strRep).mkString("List[", ",", "]"))

  implicit def nonEmptySeqStringRep[A: StringRep] = from[NonEmptySeq[A]](_.map(StringRep[A].strRep).mkString("NES(", ",", ")"))

  implicit def eitherStringRep[A: StringRep, B: StringRep] =
    from[Either[A, B]](_.fold(l => s"Left(${StringRep[A].strRep(l)})", r => s"Right(${StringRep[B].strRep(r)})"))

  implicit def optionStringRep[A: StringRep] = from[Option[A]](_.fold("None")(v => s"Some(${StringRep[A].strRep(v)})"))

  implicit def pairStringRep[A: StringRep, B: StringRep] =
    from[(A, B)](pair => s"(${StringRep[A].strRep(pair._1)}, ${StringRep[B].strRep(pair._2)})")

  implicit def mapStringRep[A: StringRep, B: StringRep] =
    from[Map[A, B]](_.map { case (k, v) =>  s"${StringRep[A].strRep(k)} -> ${StringRep[B].strRep(v)}" }.mkString("Map(", ",", ")"))
}