package boon
package printers

import scala.compat.Platform.EOL

import boon.model.Failed
import boon.model.Passed
import boon.result.SuiteOutput
import boon.result.PassedOutput
import boon.result.FailedOutput
import boon.result.TestOutput
import boon.result.SequentialFailedOutput
import boon.result.SequentialPassedOutput
import boon.result.AssertionOutput
import boon.result.SequentialFailData
import boon.result.TestPassedOutput
import boon.result.TestThrewOutput
import boon.result.Trace


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
    case TestPassedOutput(name, assertions, pass) =>
      val token = pass match {
        case Passed => ps.test.tokens.passed
        case Failed => ps.test.tokens.failed
      }

      val colouredTestName = ps.test.colour(name)

      s"${ps.test.padding} - ${colouredTestName} ${token}${EOL}" +
        assertions.map(assertionOutputString(_, ps)).toSeq.mkString(EOL)

    case TestThrewOutput(name, error, trace, loc) =>
      val token = ps.test.tokens.failed
      val colouredTestName = ps.test.colour(name)

      s"${ps.test.padding} - ${colouredTestName} ${token}${EOL}" +
      exceptionTrace(ps, trace) +
      s"${ps.assertion.failedPadding} ${ps.colourError(s"=> ${error}")} ${loc}"
  }

  private def exceptionTrace(ps: PrinterSetting, trace: Seq[Trace]): String = {
    if (trace.nonEmpty) {
      s"${ps.assertion.failedPadding} ${ps.colourError("!!Exception thrown!!")}${EOL}" +
        trace.map(traceString).
          mkString(s" ${ps.assertion.failedPadding}> ",
                   s"${EOL} ${ps.assertion.failedPadding}> ",
                   EOL
          )
    } else ""
  }

  private def contextString(ps: PrinterSetting, ctx: Map[String, String], baseError: String): String = {
    if (ctx.nonEmpty) {
      s"${baseError}${EOL}" +
      s"${ps.assertion.failedContextPadding}#: " +
      s"${ctx.mkString(s"${EOL}${ps.assertion.failedContextElementPadding}")}"
    } else baseError
  }

  private def assertionOutputString(ao: AssertionOutput, ps: PrinterSetting): String = ao match {
    case PassedOutput(name)        =>
      s"${ps.assertion.padding} - ${name} ${ps.assertion.tokens.common.passed}"

    case FailedOutput(name, error, trace, ctx, loc) =>
      val location = loc.fold("")(l => s"[$l]")

      val baseError =
        s"${ps.assertion.padding} - ${name} ${ps.assertion.tokens.common.failed}${EOL}" +
        exceptionTrace(ps, trace) +
        s"${ps.assertion.failedPadding} ${ps.colourError(s"=> ${error}")} ${location}"

        contextString(ps, ctx, baseError)

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

      contextString(ps, ctx, baseError)
  }

  private def traceString(trace: Trace): String = {
    val className  = trace.className
    val fileName   = trace.fileName.getOrElse("-")
    val methodName = trace.methodName
    val lineNumber = trace.lineNumber.fold("?")(_.toString)

    s"${className}${methodName}(${fileName}:${lineNumber})"
  }

}