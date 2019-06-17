package boon

import data.NonEmptySeq
import model.TestData
import model.TestState
import internal.instances._
import result.SuiteOutput
import result.AssertionOutput
import result.TestPassedOutput
import result.TestThrewOutput
import result.TestIgnoredOutput
import syntax.collection.positional
import internal.instances._

object SuccessfulSuite extends SuiteLike("SuccessfulSuite") {

  final case class XFailedOutput(name: String)

  final case class XPassedOutput(name: String, assertions: NonEmptySeq[AssertionOutput], state: TestState)

  private implicit val boonXPassedOutput = BoonType.defaults[XPassedOutput]

  private val t1 = test("can run a successful test with assertions") {

    val so = SuccessfulTestFixture.run

    val runResult = so.tests.partition {
      case tpo@TestPassedOutput(_, _, TestState.Passed) => Left(XPassedOutput(tpo.name, tpo.assertions, tpo.state))
      case TestPassedOutput(name, _, _)       => Right(XFailedOutput(name))
      case TestThrewOutput(name, _, _, _)     => Right(XFailedOutput(name))
      case TestIgnoredOutput(name)            => Right(XFailedOutput(name))
    }

    runResult.fold[TestData]( po =>
      assertTestOutput(po),
      unexpectedFailedOutput(f => s"expected only successful tests but got: ${f}", "test type"),
      unexpectedMixedOutput((f, p) => s"expected only successful tests but got both: ${f} and ${p}", "test type")
    )
  }

  private def assertTestOutput(passed: NonEmptySeq[XPassedOutput]): TestData = {
    pass | "test type" and
    positional(passed, "passed"){
      oneOrMore(
        test1 =>
          test1.name =?= "String.length" | "test.name" and
          positional(test1.assertions, "tests"){
            oneOrMore(
              assertions1 => SuiteOutput.assertionName(assertions1) =?= "empty" | "assertion.name",
              assertions2 => SuiteOutput.assertionName(assertions2) =?= "hello" | "assertion.name"
            )
          }
        , test2 =>
          test2.name =?= "String.reverse" | "test.name" and
          positional(test2.assertions, "tests"){
            one(assertions2 => SuiteOutput.assertionName(assertions2) =?= "Hola" | "assertion.name")
          }
      )
    }
  }

  private def unexpectedFailedOutput(message: NonEmptySeq[XFailedOutput] => String,
                                     assertionName: String): NonEmptySeq[XFailedOutput] => TestData = failures => {
      fail(message(failures)) | assertionName
  }

  private def unexpectedMixedOutput(message: (NonEmptySeq[XPassedOutput], NonEmptySeq[XFailedOutput]) => String,
                                     assertionName: String): (NonEmptySeq[XPassedOutput], NonEmptySeq[XFailedOutput]) => TestData = (failures, passes) => {
      fail(message(failures, passes)) | assertionName
  }

  override val tests = NonEmptySeq.nes(t1)
}


object SuccessfulTestFixture {

  private val successfulSuite = new SuiteLike("SuccessfulSuite") {
    private val t1 = test("String.length") {
       "".length      =?= 0 | "empty" and
       "hello".length =?= 5 | "hello"
    }

    private val t2 = test("String.reverse") {
      "Hola".reverse =?= "aloH" | "Hola"
    }

    override val tests = oneOrMore(t1, t2)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(successfulSuite))
}