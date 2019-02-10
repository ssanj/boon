package boon

object Printer {

  private val testPrefix = " - "
  private val assertionPrefix = "  - "
  private val errorPrefix = "   - "
  private val errorPrefixForSingleAssertion = assertionPrefix
  private val passedToken = "[passed]"
  private val failedToken = "[failed]"

  def suiteResultOutput(suiteResult: SuiteResult): String = suiteResult match {
    case SuitePassed(SuiteResult.Passed(Suite(SuiteName(suiteName), tests), _)) =>
      passedSuiteOutput(suiteName, tests)
    case SuiteFailed(SuiteResult.Failed(Suite(SuiteName(suiteName), _), failedTests, opPassedTests)) =>
      failedSuiteOutput(suiteName, failedTests, opPassedTests)
  }

  private def passedSuiteOutput(suiteName: String, passedTests: NonEmptySeq[Test]): String =  {
      s"""$suiteName:\n""" +
        passedTests.map(t => s"${testPrefix}${t.name.value} ${passedToken}").toSeq.mkString("\n")
  }

  private def failedSuiteOutput(suiteName: String,
    failedTests: NonEmptySeq[TestResult.Failed], opPassedTests: Option[NonEmptySeq[TestResult.Passed]]): String = {
      val failedString = failedTestOutput(failedTests)
      val passedString = passedTestOutput(opPassedTests)
      val separator    = if (opPassedTests.nonEmpty) "\n" else ""
      s"""$suiteName:\n${passedString}${separator}${failedString}"""
  }

  private def passedTestOutput(opPassedTests: Option[NonEmptySeq[TestResult.Passed]]): String = {
    opPassedTests.fold("")(_.map(tr => s"${testPrefix}${tr.test.name.value} ${passedToken}").toSeq.mkString("\n"))
  }

  private def failedTestOutput(failedTests: NonEmptySeq[TestResult.Failed]): String = {
    failedTests.map(tr => s"${testPrefix}${tr.test.name.value} ${failedToken}" + assertionsOutput(tr.failures)).toSeq.mkString("\n")
  }

  private def assertionsOutput(failedAssertions: NonEmptySeq[AssertionResult.Failed]): String = {
    if (NonEmptySeq.isHeadOnly(failedAssertions)) {
      val singleAssertionFailed = failedAssertions.head
      s"\n${errorPrefixForSingleAssertion}${singleAssertionFailed.error}"
    } else {
      failedAssertions.map(fa => s"\n${assertionPrefix}${fa.name.value}\n${errorPrefix}${fa.error}").toSeq.mkString("\n")
    }
  }
}