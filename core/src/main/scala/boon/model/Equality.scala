package boon
package model


trait Equality[A] {

  def eql(a1: A, a2: A): Boolean

  def neql(a1: A, a2: A): Boolean = !eql(a1, a2)

  trait EqualityLaws {

    /* Reflexivity
     * x == x = True
     */
    def reflexive(value: A): Boolean = eql(value, value)

    /* Symmetry
     * x == y = y == x
     */
    def symmetry(value1: A, value2: A): Boolean = eql(value1, value2) == eql(value2, value1)

    /* Transitivity
     * if x == y && y == z = True, then x == z = True
     */
    def transitivity(value1: A, value2: A, value3: A): Boolean =
      !(eql(value1, value2) && eql(value2, value3)) || eql(value1, value3)

    /* Negation
     * x /= y = not (x == y)
     */
    def negation(value1: A, value2: A): Boolean =
      eql(value1, value2) || (neql(value1, value2) == !eql(value1, value2))

    /* Substitutivity
     * if x == y = True and f is a "public" function whose return type is an instance of Eq, then f x == f y = True
     */
    def substitutivity[B](value1: A, value2: A, f: A => B): Boolean =
      eql(value1, value2) && f(value1) == f(value2)
  }

  val laws = new EqualityLaws{}
}

object Equality {

  def apply[A: Equality]: Equality[A] = implicitly[Equality[A]]

  def from[A](f: (A, A) => Boolean): Equality[A] = new Equality[A] {
    override def eql(a1: A, a2: A): Boolean = f(a1, a2)
  }

  def genericEquality[A] = from[A](_ == _)

  implicit val intEquality     = genericEquality[Int]
  implicit val longEquality    = genericEquality[Long]
  implicit val booleanEquality = genericEquality[Boolean]
  implicit val stringEquality  = genericEquality[String]
  implicit val floatEquality   = genericEquality[Float]
  implicit val doubleEquality  = genericEquality[Double]
  implicit val charEquality    = genericEquality[Char]

  implicit def listEquality[A: Equality] = from[List[A]] { (xs, ys) =>
    if (xs.length == ys.length) {
      val E = Equality[A]
      xs.zip(ys).forall(p => E.eql(p._1, p._2))
    } else false
  }

  implicit def nonEmptySeqEquality[A](implicit EL: Equality[List[A]]) =
    from[NonEmptySeq[A]] { (xs, ys) =>
      EL.eql(xs.toList, ys.toList)
    }

  implicit def optionEquality[A: Equality] = from[Option[A]] {
    case (Some(x), Some(y)) => Equality[A].eql(x, y)
    case (None, None)       => true
    case _                  => false
  }

  implicit def eitherEquality[A: Equality, B: Equality] = from[Either[A, B]] {
    case (Left(x), Left(y))   => Equality[A].eql(x, y)
    case (Right(x), Right(y)) => Equality[B].eql(x, y)
    case _                    => false
  }

  implicit def pairEquality[A: Equality, B: Equality] = from[(A, B)] {
    case ((x1, y1), (x2, y2)) => Equality[A].eql(x1, x2) && Equality[B].eql(y1, y2)
  }

  implicit def mapEquality[A: Ordering: Equality, B: Ordering: Equality] = from[Map[A, B]] { (xs, ys) =>
   (xs.isEmpty && ys.isEmpty) ||
      (xs.size == ys.size && {
        {
          import scala.collection.immutable.TreeSet
          (TreeSet.empty[A] ++ xs.keySet).zip((TreeSet.empty[A] ++ ys.keySet)).forall(x => Equality[A].eql(x._1, x._2)) &&
          {
            val xValues = xs.values.toSeq
            val yValues = ys.values.toSeq

            (xValues.isEmpty && yValues.isEmpty) ||
            (xs.values.toSeq.sorted[B].zip(ys.values.toSeq.sorted[B]).forall(x => Equality[B].eql(x._1, x._2)))
          }
        }
      })
  }

}