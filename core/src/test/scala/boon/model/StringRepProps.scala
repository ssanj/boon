package boon
package model

import boon.data.NonEmptySeq
import scalacheck.DataArb._
import org.scalacheck.Properties
import scala.reflect.runtime.universe._
import scala.util.Try

object StringRepProps extends Properties("Equality") with StringRepLawDefinition {

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

  strRepLaws[Try[Int]]
  strRepLaws[Try[Long]]
  strRepLaws[Try[String]]
  strRepLaws[Try[Boolean]]
  strRepLaws[Try[Float]]
  strRepLaws[Try[Double]]
  strRepLaws[Try[Char]]

  strRepLaws[(Int, Int)]
  strRepLaws[(Int, String)]
  strRepLaws[(Boolean, Double)]
  strRepLaws[(Char, Option[String])]

  strRepLaws[(Int, Int, Int)]
  strRepLaws[(Boolean, Double, String)]

  strRepLaws[(Int, Int, Int, Int)]
  strRepLaws[(Boolean, Double, String, Int)]

  strRepLaws[Map[Int, String]]
  strRepLaws[Map[String, String]]
  strRepLaws[Map[Long, Char]]
  
  strRepLaws[Throwable]
}
