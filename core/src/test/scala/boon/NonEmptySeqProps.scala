package boon

import boon.data.NonEmptySeq
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Arbitrary._
import scalacheck.DataArb._

import scala.reflect.runtime.universe._

object NonEmptySeqProps extends Properties("NonEmptySeq") {

  mapLaws[String, Int]
  mapLaws[String, Double]
  mapLaws[Int, String]
  mapLaws[Double, Char]

  toListLaws[String]
  toListLaws[Int]
  toListLaws[Double]
  toListLaws[Char]

  private def toListLaws[A: Arbitrary](implicit typeTagA: TypeTag[A]): Unit = {
    val typeAName = typeOf[A].toString
    property(s"toList[${typeAName}]") = Prop.forAll(toList[A] _)
  }

  private def mapLaws[A: Arbitrary : Cogen, B: Arbitrary](implicit typeTagA: TypeTag[A], typeTagB: TypeTag[B]): Unit = {
    val typeAName = typeOf[A].toString
    val typeBName = typeOf[B].toString

    property(s"map(${typeAName} => ${typeBName})") = Prop.forAll(mappingOnList[A, B] _)
  }

   //converting to a List is the same as appending the `head` to the tail as a List
   private def toList[A](inputs: NonEmptySeq[A]): Boolean = {
    inputs.toList == (inputs.head :: inputs.tail.toList)
  }

  //mapping on List is the same as mapping on NES
  private def mappingOnList[A, B](inputs: NonEmptySeq[A], f: A => B): Boolean = {
    inputs.map(f).toList == inputs.toList.map(f)
  }
}