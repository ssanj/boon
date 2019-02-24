package boon
package printers

final case class DeferredTestResult(test: DeferredTest, assertionResults: NonEmptySeq[Defer[AssertionResult]])

final case class DeferredSuiteResult(suite: DeferredSuite, testResults: NonEmptySeq[DeferredTestResult])

object IncrementalOutput {

  import scala.util.Try

  private def runAssertion(dAssertion: Defer[Assertion]): Defer[AssertionResult] = {
    dAssertion.flatMap { assertion =>
      Try {
        for {
          testable <- assertion.testable
          value1   <- testable.value1
          value2   <- testable.value2
        } yield {
          if (testable.equality.eql(value1, value2)) AssertionPassed(assertion)
          else AssertionFailed(AssertionError(assertion, testable.difference.diff(value1, value2)))
        }
      }.fold[Defer[AssertionResult]](error => Defer(() => AssertionThrew(assertion.name, error)), identity _)
    }
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