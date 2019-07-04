package boon
package model

import boon.data.NonEmptySeq
import scalacheck.DataArb._
import scalacheck.GeneralArb._
import org.scalacheck.Properties
import org.scalacheck._

object EqualityProps extends Properties("Equality") with EqualityLawDefinition {

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
  
  equalityLaws[Throwable]
  equalityLaws[Null.type]
  equalityLaws[Plain]
}
