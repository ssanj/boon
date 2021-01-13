package boon

import boon.model.TestIgnoredResult
import boon.model.TestThrewResult
import boon.model.ThrownTest
import boon.model.StoppedOnFirstFailed
import boon.model.FirstFailed
import boon.model.AllPassed
import boon.model.CompositeTestResult
import boon.model.SequentialPass
import boon.data.NonEmptySeq
import boon.model.TestName
import boon.model.SingleTestResult
import boon.model.AssertionResult
import boon.model.DeferredTest
import boon.model.TestResult
import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.propBoolean
import scalacheck.ModelArb._
import scalacheck.DataArb._

object TestNameProps extends Properties("TestName") with Arbs {

  final case class SingleTestResultTestName(name: TestName, testResult: TestResult)
  final case class CompositeTestResultAllPassedTestName(name: TestName, testResult: TestResult)
  final case class CompositeTestResultStoppedOnFirstTestName(name: TestName, testResult: TestResult)
  final case class TestThrewResultTestName(name: TestName, testResult: TestResult)
  final case class TestIgnoredTestName(name: TestName, testResult: TestResult)

  testNameProperty[SingleTestResultTestName]("SingleTestResult", _.name, _.testResult)
  testNameProperty[CompositeTestResultAllPassedTestName]("CompositeTestResult.AllPassed", _.name, _.testResult)
  testNameProperty[CompositeTestResultStoppedOnFirstTestName]("CompositeTestResult.StoppedOnFirst", _.name, _.testResult)
  testNameProperty[TestThrewResultTestName]("TestThrewResult", _.name, _.testResult)
  testNameProperty[TestIgnoredTestName]("TestIgnoredTest", _.name, _.testResult)

  def testNameProperty[T: Arbitrary](propertyName: String, getName: T => TestName, getTestResult: T => TestResult): Unit =  {
    property(propertyName) =
      Prop.forAll { instance: T =>
        val name = getName(instance)
        val actualName = TestResult.testName(getTestResult(instance))
        (actualName == name) :| s"Expected test name of ${name} but got: ${actualName}"
      }
  }
}

//A simple way to not see the implicits at the top of the file.
//Unfortunately the implicits have to be defined before their usage point
//Which obscures the whole test
trait Arbs {
  implicit val singleTestResultTestNameArb: Arbitrary[TestNameProps.SingleTestResultTestName] = Arbitrary (
    for {
      test             <- arbitrary[DeferredTest]
      assertionResults <- arbitrary[NonEmptySeq[AssertionResult]]
    } yield TestNameProps.SingleTestResultTestName(test.name, SingleTestResult(test, assertionResults))
  )

  implicit val compositeTestResultAllPassedTestNameArbitrary: Arbitrary[TestNameProps.CompositeTestResultAllPassedTestName] = Arbitrary {
    for {
      name <- arbitrary[TestName]
      pass <- arbitrary[NonEmptySeq[SequentialPass]]
    } yield TestNameProps.CompositeTestResultAllPassedTestName(name, CompositeTestResult(AllPassed(name, pass)))
  }

  implicit val compositeTestResultStoppedOnFirstTestNameArbitrary: Arbitrary[TestNameProps.CompositeTestResultStoppedOnFirstTestName] = Arbitrary {
    for {
      name <- arbitrary[TestName]
      first <- arbitrary[FirstFailed]
    } yield TestNameProps.CompositeTestResultStoppedOnFirstTestName(name, CompositeTestResult(StoppedOnFirstFailed(name, first)))
  }

  implicit val testThrewResultTestName: Arbitrary[TestNameProps.TestThrewResultTestName] = Arbitrary {
    for {
      test <- arbitrary[ThrownTest]
    } yield TestNameProps.TestThrewResultTestName(test.name, TestThrewResult(test))
  }

  implicit val testIgnoredTestName: Arbitrary[TestNameProps.TestIgnoredTestName] = Arbitrary {
    for {
      name <- arbitrary[TestName]
    } yield TestNameProps.TestIgnoredTestName(name, TestIgnoredResult(name))
  }
}