package boon

trait Equality[A] {
  def eql(a1: A, a2: A): Boolean
}


object Equality {

  def apply[A: Equality]: Equality[A] = implicitly[Equality[A]]

  implicit object IntEquality extends Equality[Int] {
    override def eql(a1: Int, a2: Int): Boolean = a1 == a2
  }

  implicit object LongEquality extends Equality[Long] {
    override def eql(a1: Long, a2: Long): Boolean = a1 == a2
  }

  implicit object BooleanEquality extends Equality[Boolean] {
    override def eql(a1: Boolean, a2: Boolean): Boolean = a1 == a2
  }

  implicit object StringEquality extends Equality[String] {
    override def eql(a1: String, a2: String): Boolean = a1 == a2
  }

  implicit object FloatEquality extends Equality[Float] {
    override def eql(a1: Float, a2: Float): Boolean = a1 == a2
  }

  implicit object DoubleEquality extends Equality[Double] {
    override def eql(a1: Double, a2: Double): Boolean = a1 == a2
  }

  implicit object CharEquality extends Equality[Char] {
    override def eql(a1: Char, a2: Char): Boolean = a1 == a2
  }

  implicit def listEquality[A](implicit E: Equality[A]): Equality[List[A]] = new Equality[List[A]] {
    override def eql(xs: List[A], ys: List[A]): Boolean = (xs, ys) match {
      case (_ :: _, Nil) => false
      case (Nil, _ :: _) => false
      case _ => xs.zip(ys).forall(p => E.eql(p._1, p._2))
    }
  }
}