package boon

//  SuiteName [passed]
//  - test1 [passed]
//    - assertion1 [/]
//    - assertion2 [/]
// - test2 [failed]
//    - assertion1 [x]
//      => [] != ["studio"]
//    - assertion2 [/]
//

import Boon.suiteResultToPassable
import Boon.testResultToPassable

final case class SuiteOutput(name: String, tests: NonEmptySeq[TestOutput], pass: Passable)
final case class TestOutput(name: String, assertions: NonEmptySeq[AssertionOutput], pass: Passable)

sealed trait AssertionOutput extends Product with Serializable
final case class PassedOutput(name: String) extends AssertionOutput
final case class FailedOutput(name: String, error: String) extends AssertionOutput

object SuiteOutput {
  def toSuiteOutput(suiteResult: SuiteResult): SuiteOutput = {
    val testOutputs = suiteResult.testResults.map { tr =>
      val assertionOutputs = tr.assertionResults.map {
        case AssertionPassed(Assertion(AssertionName(name), _)) => PassedOutput(name)
        case AssertionFailed(AssertionError(Assertion(AssertionName(name), _), error)) => FailedOutput(name, error)
      }

      TestOutput(tr.test.name.value, assertionOutputs, testResultToPassable(tr))
    }

    SuiteOutput(suiteResult.suite.name.value, testOutputs, suiteResultToPassable(suiteResult))
  }
}