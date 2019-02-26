package boon
package printers

import scala.compat.Platform.EOL


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
      s"${ps.assertion.padding} - ${name} ${ps.assertion.tokens.passed}"
    case FailedOutput(name, error, ctx) =>
      val baseError =
        s"${ps.assertion.padding} - ${name} ${ps.assertion.tokens.failed}${EOL}" +
        s"${ps.assertion.failedPadding} " +
        s"${ps.colourError(s"=> ${error}")}"

      if (ctx.nonEmpty) {
        s"${baseError}${EOL}" +
        s"${ps.assertion.failedContextPadding}#: " +
        s"${ctx.mkString(s"${EOL}${ps.assertion.failedContextElementPadding}")}"
      } else baseError
  }

}