package boon

trait Difference[A] {
  def diff(a1: A, a2: A): String
}

object Difference {

  def apply[A: Difference]: Difference[A] = implicitly[Difference[A]]

  private def genericDifference[A](implicit rep: StringRep[A]): Difference[A] = new Difference[A] {
    def diff(a1: A, a2: A): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
  }

  implicit object IntDifference extends Difference[Int] {
    def diff(a1: Int, a2: Int): String = genericDifference[Int].diff(a1, a2)
  }

  implicit object LongDifference extends Difference[Long] {
    def diff(a1: Long, a2: Long): String = genericDifference[Long].diff(a1, a2)
  }

  implicit object FloatDifference extends Difference[Float] {
    def diff(a1: Float, a2: Float): String = genericDifference[Float].diff(a1, a2)
  }

  implicit object DoubleDifference extends Difference[Double] {
    def diff(a1: Double, a2: Double): String = genericDifference[Double].diff(a1, a2)
  }

  implicit object BooleanDifference extends Difference[Boolean] {
    val rep = StringRep[Boolean]
    def diff(a1: Boolean, a2: Boolean): String = s"${rep.strRep(a1)} is not ${rep.strRep(a2)}"
  }

  implicit object StringDifference extends Difference[String] {
    val rep = StringRep[String]
    def diff(a1: String, a2: String): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
  }

  implicit object CharDifference extends Difference[Char] {
    val rep = StringRep[Char]
    def diff(a1: Char, a2: Char): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
  }

  implicit def listDifference[A: Difference : StringRep]: Difference[List[A]] = new Difference[List[A]] {
    val rep = StringRep[List[A]]
    def diff(xs: List[A], ys: List[A]): String = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
  }
}