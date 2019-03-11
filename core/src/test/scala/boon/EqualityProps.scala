package boon

import boon.model.Equality
import boon.model.FailableAssertion
import org.scalacheck.Properties
import org.scalacheck._
import Prop.forAll
import scala.reflect.runtime.universe._
import boon.scalacheck.Arb._

object EqualityProps extends Properties("Equality") {

/* Reflexivity
 * x == x = True
 *
 * Symmetry
 * x == y = y == x
 *
 * Transitivity
 * if x == y && y == z = True, then x == z = True
 *
 * Substitutivity
 * if x == y = True and f is a "public" function whose return type is an instance of Eq, then f x == f y = True
 *
 * Negation
 * x /= y = not (x == y)
 */

  equalityLaws[Int]
  equalityLaws[Long]
  equalityLaws[Boolean]
  equalityLaws[String]
  equalityLaws[Float]
  equalityLaws[Double]
  equalityLaws[Char]

  equalityLaws[List[Int]]
  equalityLaws[List[Long]]
  equalityLaws[List[String]]
  equalityLaws[List[Boolean]]
  equalityLaws[List[Float]]
  equalityLaws[List[Double]]
  equalityLaws[List[Char]]

  equalityLaws[Option[Int]]
  equalityLaws[Option[Long]]
  equalityLaws[Option[String]]
  equalityLaws[Option[Boolean]]
  equalityLaws[Option[Float]]
  equalityLaws[Option[Double]]
  equalityLaws[Option[Char]]

  equalityLaws[(Int, Int)]
  equalityLaws[(Int, String)]
  equalityLaws[(Boolean, Double)]
  equalityLaws[(Char, Option[String])]

  equalityLaws[Map[Int, String]]
  equalityLaws[Map[String, String]]
  equalityLaws[Map[Long, Char]]
  equalityLaws[FailableAssertion]

  private def equalityLaws[A: Equality: Arbitrary](implicit typeTag: TypeTag[A]): Unit = {
    val typeName = typeOf[A].toString

    property(s"${typeName}.reflexive") = forAll(reflexiveLaw[A] _)

    property(s"${typeName}.symmetry") = forAll(symmetryLaw[A] _)

    property(s"${typeName}.transitive") = forAll(transitivityLaw[A] _)

    property(s"${typeName}.negation") = forAll(negationLaw[A] _)

    // property(s"${typeName}.substitutivity") = forAll(substitutivityLaw[A, B] _)
  }


  private def reflexiveLaw[A: Equality: Arbitrary](value: A): Boolean = {
    Equality[A].eql(value, value)
  }

  private def symmetryLaw[A: Equality](value1: A, value2: A): Boolean = {
    val eql = Equality[A].eql _
    eql(value1, value2) == eql(value2, value1)
  }

  private def transitivityLaw[A: Equality](value1: A, value2: A, value3: A): Boolean = {
    val eql = Equality[A].eql _
    !(eql(value1, value2) && eql(value2, value3)) || eql(value1, value3)
  }

  private def negationLaw[A: Equality](value1: A, value2: A): Boolean = {
    val eqA = Equality[A]
    val eql = eqA.eql _
    val neql = eqA.neql _

    eql(value1, value2) || (neql(value1, value2) == !eql(value1, value2))
  }
}
