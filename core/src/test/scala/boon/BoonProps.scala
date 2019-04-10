package boon

import model.IsEqual
import model.IsNotEqual
import model.EqualityType
import model.StringRep
import model.Testable

import org.scalacheck.Gen
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.scalacheck._

import scala.reflect.runtime.universe._

object BoonProps extends Properties("BoonProps") {

  private implicit val equalityTypeArb = Arbitrary[EqualityType](Gen.oneOf(IsEqual, IsNotEqual))

  testable[Int]
  testable[String]
  testable[Char]
  testable[Double]

  defineAssertion[Int]
  defineAssertion[String]
  defineAssertion[Char]
  defineAssertion[Double]

  private def testable[A: Arbitrary : BoonType](implicit typeTag: TypeTag[A]): Unit =  {
    val typeName = typeOf[A].toString
    property(s"${typeName}.testable") = Prop.forAll(testableProp[A] _)
  }

  private def defineAssertion[A: Arbitrary : BoonType](implicit typeTag: TypeTag[A]): Unit =  {
    val typeName = typeOf[A].toString
    property(s"${typeName}.defineAssertion") = Prop.forAll(assertionProp[A] _)
  }

  private def testableProp[A: BoonType](v1: A, v2: A, eqt: EqualityType): Prop = {
    val testable = Boon.testable[A](defer(v1), defer(v2), eqt).run
    assertTestable(testable, v1, v2, eqt)
  }

  private def assertTestable[A: BoonType](testable: Testable, v1: A, v2: A, eqt: EqualityType): Prop = {
    val value1 = testable.value1.run
    val value2 = testable.value2.run
    val rep = StringRep[A].strRep _

    (value1.asInstanceOf[A] == v1)   :| "value1"                 &&
    (value2.asInstanceOf[A] == v2)   :| "value2"                 &&
    (testable.equalityType == eqt)   :| "equality type"          &&
    (testable.equality.eql(value1, value2) == (v1 == v2 )) :| "equality"  &&
    (testable.difference.diff(value1, value2) == one(s"${rep(v1)} != ${rep(v2)}")) :| "diff"
  }

  private def assertionProp[A: BoonType](name: String, v1: A, v2: A, eqt: EqualityType, context: Map[String, String]): Prop = {
    val assertion = Boon.defineAssertion[A](name, (defer(v1), defer(v2)), eqt, context)

    (assertion.name.value == name)                      :| "name"     &&
    (assertion.context == context)                      :| "context"  &&
    assertTestable(assertion.testable.run, v1, v2, eqt) :| "testable" &&
    (assertion.location.line == 58)                     :| "location"
  }
}