package boon
package printers

import scala.compat.Platform.EOL

import boon.model.Failed
import boon.model.Passed

object SimplePrinter {

  def apply(suiteOutput: SuiteOutput, ps: PrinterSetting, output: String => Unit): Unit = {
    output(suiteOutputString(suiteOutput, ps))
  }

  private def suiteOutputString(so: SuiteOutput, ps: PrinterSetting): String = so match {
    case SuiteOutput(name, tests, pass) =>
      val token = pass match {
        case Passed => ps.suite.tokens.passed
        case Failed => ps.suite.tokens.failed
      }

      s"${name} ${token}${EOL}" +
        tests.map(testOutputString(_, ps)).toSeq.mkString(EOL)
  }

  private def testOutputString(to: TestOutput, ps: PrinterSetting): String = to match {
    case TestOutput(name, assertions, pass) =>
      val token = pass match {
        case Passed => ps.test.tokens.passed
        case Failed => ps.test.tokens.failed
      }

      val colouredTestName = ps.test.colour(name)

      s"${ps.test.padding} - ${colouredTestName} ${token}${EOL}" +
        assertions.map(assertionOutputString(_, ps)).toSeq.mkString(EOL)
  }

  private def assertionOutputString(ao: AssertionOutput, ps: PrinterSetting): String = ao match {
    case PassedOutput(name)        =>
      s"${ps.assertion.padding} - ${name} ${ps.assertion.tokens.common.passed}"

    case FailedOutput(name, error, ctx, loc) =>
      val location = loc.fold("")(l => s"[$l]")

      val baseError =
        s"${ps.assertion.padding} - ${name} ${ps.assertion.tokens.common.failed}${EOL}" +
        s"${ps.assertion.failedPadding} " +
        s"${ps.colourError(s"=> ${error}")} ${location}"

      if (ctx.nonEmpty) {
        s"${baseError}${EOL}" +
        s"${ps.assertion.failedContextPadding}#: " +
        s"${ctx.mkString(s"${EOL}${ps.assertion.failedContextElementPadding}")}"
      } else baseError

    case SequentialPassedOutput(name, passed) =>
      val compositePasses = passed.map(pa => s"${ps.assertion.padding} ${ps.assertion.compositePrefix} ${pa.name} ${ps.assertion.tokens.common.passed}").toSeq.mkString(EOL)
      s"${compositePasses}"

    case SequentialFailedOutput(name, SequentialFailData(failedName, error, ctx, loc), passed, notRun) =>
      val location = loc.fold("")(l => s"[$l]")

      val compositePasses = passed.map(pa => s"${ps.assertion.padding} ↓ ${pa.name} ${ps.assertion.tokens.common.passed}").toSeq.mkString(EOL)

      val failedAssertion = s"${ps.assertion.padding} ${ps.assertion.compositePrefix} ${failedName} ${ps.assertion.tokens.common.failed}"

      val errorReason = s"${ps.assertion.failedPadding} ${ps.colourError(s"=> ${error}")} ${location}"

      val compositeNotRun = notRun.map(nr => s"${ps.assertion.padding} ${ps.assertion.compositePrefix} ${nr.name} ${ps.assertion.tokens.notRun}").toSeq.mkString(EOL)

      val baseError =
        (if (passed.nonEmpty) s"${compositePasses}${EOL}" else "") +
        s"${failedAssertion}${EOL}" +
        s"${errorReason}" +
        (if (notRun.nonEmpty) s"${EOL}${compositeNotRun}" else "")

      if (ctx.nonEmpty) {
        s"${baseError}${EOL}" +
        s"${ps.assertion.failedContextPadding}#: " +
        s"${ctx.mkString(s"${EOL}${ps.assertion.failedContextElementPadding}")}"
      } else baseError
  }

}