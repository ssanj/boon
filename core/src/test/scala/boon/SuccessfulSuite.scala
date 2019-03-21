package boon

import syntax._
import model.Passed
import model.Passable
import model.TestData
import result.SuiteOutput
import result.AssertionOutput
import result.TestPassedOutput
import result.TestThrewOutput
import result.TestIgnoredOutput

object SuccessfulSuite extends SuiteLike("SuccessfulSuite") {

  final case class XFailedOutput(name: String)

  final case class XPassedOutput(name: String, assertions: NonEmptySeq[AssertionOutput], pass: Passable)

  private val t1 = test("can run a successful test with assertions") {

    val so = SuccessfulTestFixture.run

    val runResult = so.tests.partition {
      case tpo@TestPassedOutput(_, _, Passed) => Left(XPassedOutput(tpo.name, tpo.assertions, tpo.pass))
      case TestPassedOutput(name, _, _)       => Right(XFailedOutput(name))
      case TestThrewOutput(name, _, _, _)     => Right(XFailedOutput(name))
      case TestIgnoredOutput(name)            => Right(XFailedOutput(name))
    }

    runResult.fold(po => {
      val passed = po.toSeq

      ->|>(
        pass | "test type",

        passed.length =?= 2 | "no of tests",

        % {
          val test1 = passed(0)
          test1.name =?= "String.length" | "test1.name" and
          % {
            val assertions1 = test1.assertions.toSeq
            assertions1.length =?= 2 | "no of test1.assertions" and
            SuiteOutput.assertionName(assertions1(0)) =?= "empty" | "test1.assertion1.name" and
            SuiteOutput.assertionName(assertions1(1)) =?= "hello" | "test1.assertion2.name"
          }
        },

        % {
          val test2 = passed(1)
          test2.name =?= "String.reverse" | "test2.name" and
          % {
            val assertions2 = test2.assertions.toSeq
            assertions2.length =?= 1 | "no of test2.assertions" and
            SuiteOutput.assertionName(assertions2(0)) =?= "Hola" | "test2.assertion2.name"
          }
        }
      )

    }, unexpectedFailedOutput(f => s"expected only successful tests but got: ${f}", "test type"),
       unexpectedMixedOutput((f, p) => s"expected only successful tests but got both: ${f} and ${p}", "test type"))
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
       ("".length      =?= 0 | "empty") &
       ("hello".length =?= 5 | "hello")
    }

    private val t2 = test("String.reverse") {
      ("Hola".reverse =?= "aloH" | "Hola")
    }

    override val tests = NonEmptySeq.nes(t1, t2)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(successfulSuite))
}