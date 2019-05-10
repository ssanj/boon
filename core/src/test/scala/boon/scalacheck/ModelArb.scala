package boon
package scalacheck

import boon.data.NonEmptySeq
import boon.model.AssertionError
import boon.model.AssertionTriple
import boon.model.Assertion
import boon.model.Defer
import boon.model.IsNotEqual
import boon.model.IsEqual
import boon.model.EqualityType
import boon.model.Testable
import boon.model.AssertionName
import DataArb._

import org.scalacheck._
import Arbitrary.arbitrary

object ModelArb {

  final case class AlphaString(value: String)

  final case class KeyValueStringPair(key: String, value: String)

  implicit val alphaStringArb: Arbitrary[AlphaString] = Arbitrary(Gen.alphaStr.map(AlphaString))

  implicit val assertionNameArb: Arbitrary[AssertionName] = fromArb[AlphaString, AssertionName](alphaStr => AssertionName(alphaStr.value))

  implicit val sourceLocationArb: Arbitrary[SourceLocation] = Arbitrary {
    for {
      fileName <- Gen.option(arbitrary[AlphaString].map(_.value))
      filePath <- Gen.option(arbitrary[AlphaString].map(_.value))
      line     <- Gen.choose(1, 100)
    } yield SourceLocation(fileName, filePath, line)
  }

  implicit def equalityTypeArb: Arbitrary[EqualityType] = Arbitrary {
    Gen.oneOf(IsEqual, IsNotEqual)
  }

  implicit def testableIntArb: Arbitrary[Testable] = Arbitrary {
    Gen.oneOf(genTestable[Int],
              genTestable[String],
              genTestable[Char],
              genTestable[Float])
  }

  private def genTestable[T: Arbitrary: BoonType]: Gen[Testable] = for {
    n1  <- arbitrary[T]
    n2  <- arbitrary[T]
    eqt <- arbitrary[EqualityType]
  } yield Boon.testable[T](defer[T](n1), defer[T](n2), eqt).run()

  implicit def deferArb[T: Arbitrary]: Arbitrary[Defer[T]] =
    fromArb[T, Defer[T]](defer[T](_))

  implicit val assertionArb: Arbitrary[Assertion] = Arbitrary {
    for {
      name     <- arbitrary[AssertionName]
      testable <- arbitrary[Defer[Testable]]
      context  <- arbitrary[Map[String, String]]
      location <- arbitrary[SourceLocation]
    } yield Assertion(name, testable, context, location)
  }

  implicit val keyValueStringPairGen: Arbitrary[KeyValueStringPair] = Arbitrary {
    for {
      key <- arbitrary[AlphaString].map(_.value)
      value <- arbitrary[AlphaString].map(_.value)
    } yield KeyValueStringPair(key, value)
  }


  implicit val contextArb: Arbitrary[Map[String, String]] = Arbitrary {
    for {
      size  <- Gen.choose(1, 3)
      pairs <- Gen.listOfN(size, arbitrary[KeyValueStringPair].map(kvp => (kvp.key, kvp.value)))
    } yield Map[String, String](pairs:_*)
  }

  implicit val assertionTripleArbitrary: Arbitrary[AssertionTriple] = Arbitrary {
    for {
      name    <- arbitrary[AssertionName]
      context <- arbitrary[Map[String, String]]
      loc     <- arbitrary[SourceLocation]
    } yield AssertionTriple(name, context, loc)
  }

  implicit val assertionErrorArbitrary: Arbitrary[AssertionError] = Arbitrary {
    for {
      assertion <- arbitrary[Assertion]
      errors    <- arbitrary[NonEmptySeq[String]]
    } yield AssertionError(assertion, errors)
  }

// final case class AssertionError(assertion: Assertion, errors: NonEmptySeq[String])
// final case class AssertionThrow(name: AssertionName, value: Throwable, location: SourceLocation)

  private def fromArb[A: Arbitrary, T](f: A => T): Arbitrary[T] = Arbitrary {
    Arbitrary.arbitrary[A].map(f)
  }
}
