package boon
package model

import scalacheck.Arb._
import org.scalacheck.Properties
import org.scalacheck._
import Prop.forAll
import scala.reflect.runtime.universe._

object EqualityProps extends Properties("Equality") {

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

  equalityLaws[NonEmptySeq[Int]]
  equalityLaws[NonEmptySeq[Long]]
  equalityLaws[NonEmptySeq[String]]
  equalityLaws[NonEmptySeq[Boolean]]
  equalityLaws[NonEmptySeq[Float]]
  equalityLaws[NonEmptySeq[Double]]
  equalityLaws[NonEmptySeq[Char]]

  equalityLaws[Either[String, Int]]
  equalityLaws[Either[String, Long]]
  equalityLaws[Either[String, String]]
  equalityLaws[Either[String, Boolean]]
  equalityLaws[Either[String, Float]]
  equalityLaws[Either[String, Double]]
  equalityLaws[Either[String, Char]]
  equalityLaws[Either[Boolean, Int]]
  equalityLaws[Either[Int, String]]

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

  equalityLaws[(Int, Int, Int)]
  equalityLaws[(Boolean, Double, String)]

  equalityLaws[(Int, Int, Int, Int)]
  equalityLaws[(Boolean, Double, String, Int)]

  equalityLaws[Map[Int, String]]
  equalityLaws[Map[String, String]]
  equalityLaws[Map[Long, Char]]

  private def equalityLaws[A: Equality: Arbitrary](implicit typeTag: TypeTag[A]): Unit = {
    val typeName = typeOf[A].toString

    property(s"${typeName}.reflexive") = forAll(reflexiveLaw[A] _)

    property(s"${typeName}.symmetry") = forAll(symmetryLaw[A] _)

    property(s"${typeName}.transitive") = forAll(transitivityLaw[A] _)

    property(s"${typeName}.negation") = forAll(negationLaw[A] _)

    // property(s"${typeName}.substitutivity") = forAll(substitutivityLaw[A, B] _)
  }


  private def reflexiveLaw[A: Equality](value: A): Boolean = {
    Equality[A].laws.reflexive(value)
  }

  private def symmetryLaw[A: Equality](value1: A, value2: A): Boolean = {
    Equality[A].laws.symmetry(value1, value2)
  }

  private def transitivityLaw[A: Equality](value1: A, value2: A, value3: A): Boolean = {
    Equality[A].laws.transitivity(value1, value2, value3)
  }

  private def negationLaw[A: Equality](value1: A, value2: A): Boolean = {
    Equality[A].laws.negation(value1, value2)
  }

  // private def substitutivityLaw[A: Equality, B: Equality](value1: A, value2: A, f: A => B): Boolean = {
  //   Equality[A].laws.substitutivity(value1, value2, f)
  // }
}
