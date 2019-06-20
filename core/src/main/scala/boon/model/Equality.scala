package boon
package model

//typesafe equality
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

  def genEq[A] = from[A](_ == _)

  implicit def genericEquality[A] = genEq[A]

  implicit val throwableEquality = from[Throwable]((t1, t2) => t1.getClass == t2.getClass && t1.getMessage == t2.getMessage)
}