package boon

import Colourise.colourise
import Colourise.Green
import Colourise.Red
import Colourise.RedUnderlined
import scala.compat.Platform.EOL


object SimplePrinter {

  private val suitePassedToken = "[passed]"
  private val suiteFailedToken = "[failed]"

  private val testPadding = ""
  private val testPassedToken = "[passed]"
  private val testFailedToken = "[failed]"

  private val assertionPadding = " " * 2
  private val assertionPassedToken = "[/]"
  private val assertionFailedPadding = " " * 4
  private val assertionFailedToken = "[x]"

  def print(suiteOutput: SuiteOutput): String = {
    printSuiteOutput(suiteOutput)
  }

  private def printSuiteOutput(so: SuiteOutput): String = so match {
    case SuiteOutput(name, tests, pass) =>
      val token = pass match {
        case Passed => colourise(Some(Green) ,suitePassedToken)
        case Failed => colourise(Some(Red), suiteFailedToken)
      }

      s"${name} ${token}${EOL}" +
        tests.map(printTestOutput).toSeq.mkString(EOL)
  }

  private def printTestOutput(to: TestOutput): String = to match {
    case TestOutput(name, assertions, pass) =>
      val token = pass match {
        case Passed => colourise(Some(Green), testPassedToken)
        case Failed => colourise(Some(Red), testFailedToken)
      }
      s"${testPadding} - ${name} ${token}${EOL}" +
        assertions.map(printAssertionOutput).toSeq.mkString(EOL)
  }

  private def printAssertionOutput(ao: AssertionOutput): String = ao match {
    case PassedOutput(name)        =>
      s"${assertionPadding} - ${name} ${colourise(Some(Green), assertionPassedToken)}"
    case FailedOutput(name, error) =>
      s"${assertionPadding} - ${name} ${colourise(Some(Red), assertionFailedToken)}${EOL}" +
      s"${assertionFailedPadding} " +
      s"${colourise(Some(RedUnderlined), s"=> ${error}")}"
  }

}