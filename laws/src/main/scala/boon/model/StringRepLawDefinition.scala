package boon.model

import scala.reflect.runtime.universe._
import org.scalacheck._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

trait StringRepLawDefinition { self: Properties =>

  def strRepLaws[A: StringRep : Equality : Arbitrary](implicit typeTag: TypeTag[A]): Unit = {
    val typeName = typeOf[A].toString

    property(s"${typeName}.stability") = forAll(stabilityLaw[A] _)

    property(s"${typeName}.equalValuesHaveSameStringRep") = forAll(equalValuesHaveSameStringRepLaw[A] _)

    property(s"${typeName}.sameStringRepHasEqualValues") = forAll(sameStringRepHasEqualValues[A] _)
  }

  private val strEquality = Equality[String]
  
  private def stabilityLaw[A: StringRep](value: A): Boolean = {
    StringRep[A].stringReplaws.stability(value, strEquality)
  }

  private def equalValuesHaveSameStringRepLaw[A: StringRep : Equality](value1: A, value2: A): Boolean = {
    StringRep[A].stringReplaws.equalValuesHaveSameStringRep(value1, value2, Equality[A], strEquality)
  }

  private def sameStringRepHasEqualValues[A: StringRep : Equality](value1: A, value2: A): Boolean = {
    StringRep[A].stringReplaws.sameStringRepHasEqualValues(value1, value2, Equality[A], strEquality)
  }
}