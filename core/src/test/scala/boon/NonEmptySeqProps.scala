package boon

import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Arbitrary._
import scalacheck.Arb._

import scala.reflect.runtime.universe._

object NonEmptySeqProps extends Properties("NonEmptySeq") {

  mapLaws[String, Int]
  mapLaws[Int, String]
  mapLaws[Char, String]
  mapLaws[String, Double]

  private def mapLaws[A: Arbitrary : Cogen, B: Arbitrary](implicit typeTagA: TypeTag[A], typeTagB: TypeTag[B]): Unit = {
    val typeAName = typeOf[A].toString
    val typeBName = typeOf[B].toString

    property(s"map(${typeAName} => ${typeBName})") = Prop.forAll(mappingOnList[A, B] _)
  }

  //mapping on List is the same as mapping on NES
  private def mappingOnList[A, B](inputs: NonEmptySeq[A], f: A => B): Boolean = {
    inputs.map(f).toList == inputs.toList.map(f)
  }
}