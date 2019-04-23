package boon

import data.NonEmptySeq
import model.TestData
import model.TestState
import result.SuiteOutput
import result.AssertionOutput
import result.TestPassedOutput
import result.TestThrewOutput
import result.TestIgnoredOutput

object SuccessfulSuite extends SuiteLike("SuccessfulSuite") {

  final case class XFailedOutput(name: String)

  final case class XPassedOutput(name: String, assertions: NonEmptySeq[AssertionOutput], state: TestState)

  private val t1 = test("can run a successful test with assertions") {

    val so = SuccessfulTestFixture.run

    val runResult = so.tests.partition {
      case tpo@TestPassedOutput(_, _, TestState.Passed) => Left(XPassedOutput(tpo.name, tpo.assertions, tpo.state))
      case TestPassedOutput(name, _, _)       => Right(XFailedOutput(name))
      case TestThrewOutput(name, _, _, _)     => Right(XFailedOutput(name))
      case TestIgnoredOutput(name)            => Right(XFailedOutput(name))
    }

    runResult.fold[TestData]( po =>
      assertTestOutput(po.toSeq),
      unexpectedFailedOutput(f => s"expected only successful tests but got: ${f}", "test type"),
      unexpectedMixedOutput((f, p) => s"expected only successful tests but got both: ${f} and ${p}", "test type")
    )
  }

  private def assertTestOutput(passed: Seq[XPassedOutput]): TestData = {
    pass | "test type" and
    passed.length =?= 2 | "no of tests" and %@(passed(0)) { test1 =>
      test1.name =?= "String.length" | "test1.name" and %@(test1.assertions.toSeq) { assertions1 =>
        assertions1.length =?= 2 | "no of test1.assertions" and
        SuiteOutput.assertionName(assertions1(0)) =?= "empty" | "test1.assertion1.name" and
        SuiteOutput.assertionName(assertions1(1)) =?= "hello" | "test1.assertion2.name"
      }
    } and %@(passed(1)) { test2 =>
      test2.name =?= "String.reverse" | "test2.name" and %@(test2.assertions.toSeq) { assertions2 =>
        assertions2.length =?= 1 | "no of test2.assertions" and
        SuiteOutput.assertionName(assertions2(0)) =?= "Hola" | "test2.assertion2.name"
      }
    } seq()
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