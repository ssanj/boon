package boon

trait Equality[A] {

  def eql(a1: A, a2: A): Boolean

  def neql(a1: A, a2: A): Boolean = !eql(a1, a2)
}

trait LowPriorityEquality {
  implicit def genericEquality[A]: Equality[A] = new Equality[A] {
    override def eql(a1: A, a2: A): Boolean = a1 == a2
  }
}

object Equality extends LowPriorityEquality {

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

  implicit def optionEquality[A](implicit E: Equality[A]): Equality[Option[A]] = new Equality[Option[A]] {
    override def eql(xs: Option[A], ys: Option[A]): Boolean = (xs, ys) match {
      case (Some(x), Some(y)) => E.eql(x, y)
      case (None, None) => true
      case _ => false
    }
  }

  implicit def pairEquality[A, B](implicit EA: Equality[A], EB: Equality[B]): Equality[Tuple2[A, B]] = new Equality[Tuple2[A, B]] {
    override def eql(xs: Tuple2[A, B], ys: Tuple2[A, B]): Boolean = (xs, ys) match {
      case ((x1, y1), (x2, y2)) => EA.eql(x1, x2) && EB.eql(y1, y2)
    }
  }

  import scala.collection.immutable.TreeSet
  implicit def mapEquality[A: Ordering, B: Ordering](implicit EA: Equality[A], EB: Equality[B]): Equality[Map[A, B]] = new Equality[Map[A, B]] {
    override def eql(xs: Map[A, B], ys: Map[A, B]): Boolean = {
      (xs.isEmpty && ys.isEmpty) ||
      (xs.size == ys.size && {
        {
          (TreeSet.empty[A] ++ xs.keySet).zip((TreeSet.empty[A] ++ ys.keySet)).forall(x => EA.eql(x._1, x._2)) &&
          {
            val xValues = xs.values.toSeq
            val yValues = ys.values.toSeq

            (xValues.isEmpty && yValues.isEmpty) ||
            (xs.values.toSeq.sorted[B].zip(ys.values.toSeq.sorted[B]).forall(x => EB.eql(x._1, x._2)))
          }
        }
      })
    }
  }

  implicit def notEquality[A](implicit E: Equality[A]): Equality[Not[A]] = new Equality[Not[A]] {
    override def eql(a1: Not[A], a2: Not[A]): Boolean = E.neql(a1.value, a2.value)
  }

  implicit object FailableAssertionEquality extends Equality[FailableAssertion] {
    override def eql(a1: FailableAssertion, a2: FailableAssertion): Boolean = (a1, a2) match {
      case (FailedAssertion(r1), FailedAssertion(r2)) => StringEquality.eql(r1, r2)
      case (NotFailedAssertion, NotFailedAssertion) => true
      case _ => false
    }
  }

  implicit object PassedAssertionEquality extends Equality[PassedAssertion.type] {
    override def eql(a1: PassedAssertion.type, a2: PassedAssertion.type): Boolean = true //Always pass
  }

  implicit object BoonExEquality extends Equality[BoonEx] {
    override def eql(x1: BoonEx, x2: BoonEx): Boolean = x1 == x2
  }
}