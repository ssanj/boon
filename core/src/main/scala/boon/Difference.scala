package boon

//This may not need to be a typeclass.
trait Difference[A] {
  def diff(a1: A, a2: A): String
}

trait LowPriorityDifference {
  implicit def genericDifference[A](implicit rep: StringRep[A]): Difference[A] = new Difference[A] {
    override def diff(a1: A, a2: A): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
  }

}

object Difference extends LowPriorityDifference {

  def apply[A: Difference]: Difference[A] = implicitly[Difference[A]]

  implicit object IntDifference extends Difference[Int] {
    override def diff(a1: Int, a2: Int): String = genericDifference[Int].diff(a1, a2)
  }

  implicit object LongDifference extends Difference[Long] {
    override def diff(a1: Long, a2: Long): String = genericDifference[Long].diff(a1, a2)
  }

  implicit object FloatDifference extends Difference[Float] {
    override def diff(a1: Float, a2: Float): String = genericDifference[Float].diff(a1, a2)
  }

  implicit object DoubleDifference extends Difference[Double] {
    override def diff(a1: Double, a2: Double): String = genericDifference[Double].diff(a1, a2)
  }

  implicit object BooleanDifference extends Difference[Boolean] {
    val rep = StringRep[Boolean]
    override def diff(a1: Boolean, a2: Boolean): String = s"${rep.strRep(a1)} is not ${rep.strRep(a2)}"
  }

  implicit object StringDifference extends Difference[String] {
    val rep = StringRep[String]
    override def diff(a1: String, a2: String): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
  }

  implicit object CharDifference extends Difference[Char] {
    val rep = StringRep[Char]
    override def diff(a1: Char, a2: Char): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
  }

  implicit def listDifference[A: StringRep]: Difference[List[A]] = new Difference[List[A]] {
    val rep = StringRep[List[A]]
    override def diff(xs: List[A], ys: List[A]): String = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
  }

  implicit def optionDifference[A: StringRep]: Difference[Option[A]] = new Difference[Option[A]] {
    val rep = StringRep[Option[A]]
    override def diff(xs: Option[A], ys: Option[A]): String = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
  }

  implicit def pairDifference[A: StringRep, B: StringRep]: Difference[Tuple2[A, B]] = new Difference[Tuple2[A, B]] {
    val rep = StringRep[Tuple2[A, B]]
    override def diff(pair1: Tuple2[A, B], pair2: Tuple2[A, B]): String = s"${rep.strRep(pair1)} != ${rep.strRep(pair2)}"
  }

  implicit def mapDifference[A: StringRep, B: StringRep]: Difference[Map[A, B]] = new Difference[Map[A, B]] {
    val rep = StringRep[Map[A, B]]
    override def diff(map1: Map[A, B], map2: Map[A, B]): String = s"${rep.strRep(map1)} != ${rep.strRep(map2)}"
  }

  implicit def notDifference[A: StringRep]: Difference[Not[A]] = new Difference[Not[A]] {
    val repA = StringRep[A]
    override def diff(na1: Not[A], na2: Not[A]): String = s"${repA.strRep(na1.value)} == ${repA.strRep(na2.value)}"
  }

  //breaks the laws (if any)
  implicit object FailableAssertionDifference extends Difference[FailableAssertion] {
    override def diff(a1: FailableAssertion, a2: FailableAssertion): String = (a1, a2) match {
      case (FailedAssertion(r1), _) => s"explicit fail: $r1"
      case (PassedAssertion, FailedAssertion(r1)) => s"explicit fail: $r1"
      case (PassedAssertion, PassedAssertion)  => s"explicit pass"
    }
  }
}