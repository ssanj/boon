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
        case Passed => ps.suitePassedToken
        case Failed => ps.suiteFailedToken
      }

      s"${name} ${token}${EOL}" +
        tests.map(testOutputString(_, ps)).toSeq.mkString(EOL)
  }

  private def testOutputString(to: TestOutput, ps: PrinterSetting): String = to match {
    case TestOutput(name, assertions, pass) =>
      val token = pass match {
        case Passed => ps.testPassedToken
        case Failed => ps.testFailedToken
      }
      s"${ps.testPadding} - ${name} ${token}${EOL}" +
        assertions.map(assertionOutputString(_, ps)).toSeq.mkString(EOL)
  }

  private def assertionOutputString(ao: AssertionOutput, ps: PrinterSetting): String = ao match {
    case PassedOutput(name)        =>
      s"${ps.assertionPadding} - ${name} ${ps.assertionPassedToken}"
    case FailedOutput(name, error, ctx) =>
      val baseError =
        s"${ps.assertionPadding} - ${name} ${ps.assertionFailedToken}${EOL}" +
        s"${ps.assertionFailedPadding} " +
        s"${ps.colourError(s"=> ${error}")}"

      if (ctx.nonEmpty) {
        s"${baseError}${EOL}" +
        s"${ps.assertionFailedContextPadding}#: " +
        s"${ctx.mkString(s"${EOL}${ps.assertionFailedContextElementPadding}")}"
      } else baseError
  }

}