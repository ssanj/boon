package boon

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
        case Passed => suitePassedToken
        case Failed => suiteFailedToken
      }

      s"${name} ${token}\n" +
        tests.map(printTestOutput).toSeq.mkString("\n")
  }

  private def printTestOutput(to: TestOutput): String = to match {
    case TestOutput(name, assertions, pass) =>
      val token = pass match {
        case Passed => testPassedToken
        case Failed => testFailedToken
      }
      s"${testPadding} - ${name} ${token}\n" +
        assertions.map(printAssertionOutput).toSeq.mkString("\n")
  }

  private def printAssertionOutput(ao: AssertionOutput): String = ao match {
    case PassedOutput(name)        =>
      s"${assertionPadding} - ${name} ${assertionPassedToken}"
    case FailedOutput(name, error) =>
      s"${assertionPadding} - ${name} ${assertionFailedToken}\n" +
      s"${assertionFailedPadding} => ${error}"
  }

}