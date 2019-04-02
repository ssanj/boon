package boon

import boon.model.Equality
import boon.model.StringRep

import scalacheck.Arb._
import org.scalacheck.Properties
import org.scalacheck._
import Prop.forAll
import scala.reflect.runtime.universe._

object StringRepProps extends Properties("Equality") {

  strRepLaws[Int]
  strRepLaws[Long]
  strRepLaws[Boolean]
  strRepLaws[String]
  strRepLaws[Float]
  strRepLaws[Double]
  strRepLaws[Char]

  strRepLaws[List[Int]]
  strRepLaws[List[Long]]
  strRepLaws[List[String]]
  strRepLaws[List[Boolean]]
  strRepLaws[List[Float]]
  strRepLaws[List[Double]]
  strRepLaws[List[Char]]

  strRepLaws[NonEmptySeq[Int]]
  strRepLaws[NonEmptySeq[Long]]
  strRepLaws[NonEmptySeq[String]]
  strRepLaws[NonEmptySeq[Boolean]]
  strRepLaws[NonEmptySeq[Float]]
  strRepLaws[NonEmptySeq[Double]]
  strRepLaws[NonEmptySeq[Char]]

  strRepLaws[Either[String, Int]]
  strRepLaws[Either[String, Long]]
  strRepLaws[Either[String, String]]
  strRepLaws[Either[String, Boolean]]
  strRepLaws[Either[String, Float]]
  strRepLaws[Either[String, Double]]
  strRepLaws[Either[String, Char]]
  strRepLaws[Either[Boolean, Int]]
  strRepLaws[Either[Int, String]]

  strRepLaws[Option[Int]]
  strRepLaws[Option[Long]]
  strRepLaws[Option[String]]
  strRepLaws[Option[Boolean]]
  strRepLaws[Option[Float]]
  strRepLaws[Option[Double]]
  strRepLaws[Option[Char]]

  strRepLaws[(Int, Int)]
  strRepLaws[(Int, String)]
  strRepLaws[(Boolean, Double)]
  strRepLaws[(Char, Option[String])]

  strRepLaws[Map[Int, String]]
  strRepLaws[Map[String, String]]
  strRepLaws[Map[Long, Char]]

  private val strEquality = Equality[String]

  private def strRepLaws[A: StringRep : Equality : Arbitrary](implicit typeTag: TypeTag[A]): Unit = {
    val typeName = typeOf[A].toString

    property(s"${typeName}.stability") = forAll(stabilityLaw[A] _)

    property(s"${typeName}.equalValuesHaveSameStringRep") = forAll(equalValuesHaveSameStringRepLaw[A] _)
  }

  private def stabilityLaw[A: StringRep](value: A): Boolean = {
    StringRep[A].stringReplaws.stability(value, strEquality)
  }

  private def equalValuesHaveSameStringRepLaw[A: StringRep : Equality](value1: A, value2: A): Boolean = {
    StringRep[A].stringReplaws.equalValuesHaveSameStringRep(value1, value2, Equality[A], strEquality)
  }
}
