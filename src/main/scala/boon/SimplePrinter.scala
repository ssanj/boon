package boon

import scala.compat.Platform.EOL


object SimplePrinter {

  def print(suiteOutput: SuiteOutput, ps: PrinterSetting): String = {
    printSuiteOutput(suiteOutput, ps)
  }

  private def printSuiteOutput(so: SuiteOutput, ps: PrinterSetting): String = so match {
    case SuiteOutput(name, tests, pass) =>
      val token = pass match {
        case Passed => ps.suitePassedToken
        case Failed => ps.suiteFailedToken
      }

      s"${name} ${token}${EOL}" +
        tests.map(printTestOutput(_, ps)).toSeq.mkString(EOL)
  }

  private def printTestOutput(to: TestOutput, ps: PrinterSetting): String = to match {
    case TestOutput(name, assertions, pass) =>
      val token = pass match {
        case Passed => ps.testPassedToken
        case Failed => ps.testFailedToken
      }
      s"${ps.testPadding} - ${name} ${token}${EOL}" +
        assertions.map(printAssertionOutput(_, ps)).toSeq.mkString(EOL)
  }

  private def printAssertionOutput(ao: AssertionOutput, ps: PrinterSetting): String = ao match {
    case PassedOutput(name)        =>
      s"${ps.assertionPadding} - ${name} ${ps.assertionPassedToken}"
    case FailedOutput(name, error) =>
      s"${ps.assertionPadding} - ${name} ${ps.assertionFailedToken}${EOL}" +
      s"${ps.assertionFailedPadding} " +
      s"${ps.colourError(s"=> ${error}")}"
  }

}