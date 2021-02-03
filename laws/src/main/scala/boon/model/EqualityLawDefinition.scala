package boon.model

import scala.reflect.runtime.universe._
import org.scalacheck._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

trait EqualityLawDefinition { self: Properties =>

  def equalityLaws[A: Equality: Arbitrary](implicit typeTag: TypeTag[A]): Unit = {
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