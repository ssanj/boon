package boon
package scalacheck

import boon.model.FirstFailed
import boon.model.TestData
import boon.model.AssertionState
import boon.model.Sequential
import boon.model.Independent
import boon.model.AssertionCombinator
import boon.model.SingleAssertionResult
import boon.model.AssertionResultThrew
import boon.model.AssertionResultFailed
import boon.model.AssertionResultState
import boon.model.AssertionResultFailed
import boon.model.AssertionResultPassed
import boon.model.AssertionFailure
import boon.model.SequentialThrew
import boon.model.SequentialFail
import boon.model.SequentialPass
import boon.model.SequentialNotRun
import boon.model.AssertionThrew
import boon.model.AssertionFailed
import boon.model.AssertionThrow
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
import GeneralArb._

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

  implicit val assertionThrowArbitrary: Arbitrary[AssertionThrow] = Arbitrary {
    for {
      name  <- arbitrary[AssertionName]
      error <- arbitrary[Throwable]
      loc   <- arbitrary[SourceLocation]
    } yield AssertionThrow(name, error, loc)
  }

  implicit val assertionFailureArbitrary: Arbitrary[AssertionFailure] = Arbitrary {
    Gen.oneOf(assertionFailedGen, assertionThrewGen)
  }

  def assertionFailedGen: Gen[AssertionFailure] =
    fromArb[AssertionError, AssertionFailed](AssertionFailed).arbitrary

  def assertionThrewGen: Gen[AssertionFailure] =
    fromArb[AssertionThrow, AssertionThrew](AssertionThrew).arbitrary

  implicit val sequentArbitrary: Arbitrary[SequentialNotRun] =
    fromArb[AssertionName, SequentialNotRun](SequentialNotRun)

  implicit val sequentialPassArbitrary: Arbitrary[SequentialPass] =
    fromArb[AssertionName, SequentialPass](SequentialPass)

  implicit val sequentialFailArbitrary: Arbitrary[SequentialFail] =
    fromArb[AssertionError, SequentialFail](SequentialFail)

  implicit val sequentialThrewArbitrary: Arbitrary[SequentialThrew] =
    fromArb[AssertionThrow, SequentialThrew](SequentialThrew)

  def assertionResultPassedGen: Gen[AssertionResultPassed] =
    fromArb[AssertionTriple, AssertionResultPassed](AssertionResultPassed).arbitrary

  def assertionResultFailedGen: Gen[AssertionResultFailed] =
    fromArb[AssertionError, AssertionResultFailed](AssertionResultFailed).arbitrary

  def assertionResultThrewGen: Gen[AssertionResultThrew] =
    fromArb[AssertionThrow, AssertionResultThrew](AssertionResultThrew).arbitrary

  implicit val assertionResultStateArbitrary: Arbitrary[AssertionResultState] = Arbitrary {
    Gen.oneOf(assertionResultPassedGen,
              assertionResultFailedGen,
              assertionResultThrewGen)
  }

  implicit val singleAssertionResultArbitrary: Arbitrary[SingleAssertionResult] =
    fromArb[AssertionResultState, SingleAssertionResult](SingleAssertionResult)

  implicit val assertionCombinatorArbitrary: Arbitrary[AssertionCombinator] = Arbitrary {
    Gen.oneOf(Independent, Sequential)
  }

  implicit val assertionStateArbitrary: Arbitrary[AssertionState] = Arbitrary {
    Gen.oneOf(AssertionState.Passed, AssertionState.Failed)
  }

  implicit val testDataArbitrary: Arbitrary[TestData] = Arbitrary {
    for {
      assertion  <- arbitrary[NonEmptySeq[Assertion]]
      combinator <- arbitrary[AssertionCombinator]
    } yield TestData(assertion, combinator)
  }

  implicit val firstFailedArbitrary: Arbitrary[FirstFailed] = Arbitrary {
    for {
      name   <- arbitrary[AssertionName]
      pass   <- arbitrary[List[SequentialPass]]
      failed <- eitherArb[SequentialFail, SequentialThrew].arbitrary
      notRun <- arbitrary[List[SequentialNotRun]]
    } yield FirstFailed(name, failed, pass, notRun)
  }

  private def fromArb[A: Arbitrary, T](f: A => T): Arbitrary[T] = Arbitrary {
    Arbitrary.arbitrary[A].map(f)
  }

  private def eitherArb[A: Arbitrary, B: Arbitrary]: Arbitrary[Either[A, B]] = Arbitrary {
    Gen.oneOf(arbitrary[A].map(Left[A, B]), arbitrary[B].map(Right[A, B]))
  }
}
