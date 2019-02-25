package boon
package printers

final case class DeferredAssertionResult(assertion: Assertion, result: Defer[AssertionResult])

final case class DeferredTestResult(test: DeferredTest, assertionResults: NonEmptySeq[DeferredAssertionResult])

final case class DeferredSuiteResult(suite: DeferredSuite, testResults: NonEmptySeq[DeferredTestResult])

object IncrementalOutput {

  import scala.util.Try

  private def runAssertion(assertion: Assertion): DeferredAssertionResult = {
      Try {
        val dresult: Defer[AssertionResult] =
          for {
            testable <- assertion.testable
            value1   <- testable.value1
            value2   <- testable.value2
          } yield {
            if (testable.equality.eql(value1, value2)) AssertionPassed(assertion)
            else AssertionFailed(AssertionError(assertion, testable.difference.diff(value1, value2)))
          }

          dresult

      }.fold[DeferredAssertionResult](
        error => DeferredAssertionResult(assertion, Defer(() => AssertionThrew(assertion.name, error))),
        DeferredAssertionResult(assertion, _))
  }

  private def runTest(dTest: DeferredTest): DeferredTestResult = {
    val assertionResults = dTest.assertions.map(runAssertion)
    DeferredTestResult(dTest, assertionResults)
  }

  private def runSuite(dSuite: DeferredSuite): DeferredSuiteResult = {
    val testResults = dSuite.tests.map(runTest)
    DeferredSuiteResult(dSuite, testResults)
  }

  def run(dSuite: DeferredSuite): DeferredSuiteResult = {
    runSuite(dSuite)
  }
}