package boon

// import Boon.suiteResultToPassable
// import Boon.testResultToPassable

object Printer {

  // private val testPrefix = " - "
  // private val assertionPrefix = "   ==> "
  // private val errorPrefix = "   - "
  // private val errorPrefixForSingleAssertion = assertionPrefix
  // private val passedToken = "[passed]"
  // private val failedToken = "[failed]"

  def suiteResultOutput(suiteResult: SuiteResult): String = ""
  // suiteResultToPassable(suiteResult) match {
  //   case Passed => passedSuiteOutput(suiteResult)
  //   case Failed => failedSuiteOutput(suiteResult)
  // }

  // private def passedSuiteOutput(suiteResult: SuiteResult): String =  {
  //     val suiteName = suiteResult.suite.name.value
  //     val tests = suiteResult.testResults.map(_.test)

  //     s"""$suiteName:\n""" +
  //       tests.map(t => s"${testPrefix}${t.name.value} ${passedToken}").toSeq.mkString("\n")
  // }

  // private def failedSuiteOutput(suiteResult: SuiteResult): String = {
  //     val suiteName = suiteResult.suite.name.value

  //     val testOutput =
  //       suiteResult.testResults.map(tr => (tr, testResultToPassable(tr))).map {
  //         case (tr, Passed) => passedTestOutput(tr)
  //         case (tr, Failed) => failedTestOutput(tr)
  //       }

  //     val testOutputString = testOutput.toSeq.mkString("\n")

  //     s"$suiteName:\n$testOutputString"
  // }

  // private def passedTestOutput(tr: TestResult): String = {
  //   s"${testPrefix}${tr.test.name.value} ${passedToken}"
  // }

  // private def failedTestOutput(tr: TestResult): String = {

  //   val assertionPartition: These[NonEmptySeq[AssertionError], NonEmptySeq[Unit]] =
  //     tr.assertionResults.partition[AssertionError, Unit] {
  //       case AssertionFailed(assertionError) => Left(assertionError)
  //       case _ => Right(())
  //     }

  //   val assertionString =
  //     assertionPartition match {
  //       case These.OnlyLeft(assertionErrors) => assertionsOutput(assertionErrors)
  //       case _ => ""
  //     }

  //   s"${testPrefix}${tr.test.name.value} ${failedToken}${assertionString}"
  // }

  // private def assertionsOutput(assertionErrors: NonEmptySeq[AssertionError]): String = {
  //   if (NonEmptySeq.isHeadOnly(assertionErrors)) {
  //     val singleAssertionFailed = assertionErrors.head
  //     s"\n${errorPrefixForSingleAssertion}${singleAssertionFailed.error} [x]"
  //   } else {
  //     assertionErrors.map(fa => s"\n${assertionPrefix}${fa.assertion.name.value}\n${errorPrefix}${fa.error} [x]").toSeq.mkString("\n")
  //   }
  // }
}