package boon
package printers

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
import Colourise.colourise
import Colourise.red
import Colourise.green
import Colourise.redU

final case class SuiteOutput(name: String, tests: NonEmptySeq[TestOutput], pass: Passable)
final case class TestOutput(name: String, assertions: NonEmptySeq[AssertionOutput], pass: Passable)

sealed trait AssertionOutput extends Product with Serializable
final case class PassedOutput(name: String) extends AssertionOutput
final case class FailedOutput(name: String, error: String, context: Map[String, String]) extends AssertionOutput

object SuiteOutput {
  // DeferredSuite -> A
  def toSuiteOutput(suiteResult: SuiteResult): SuiteOutput = {
    val testOutputs = suiteResult.testResults.map { tr =>
      val assertionOutputs = tr.assertionResults.map {
        case AssertionPassed(Assertion(AssertionName(name), _, _)) => PassedOutput(name)
        case AssertionFailed(AssertionError(Assertion(AssertionName(name), _, ctx), error)) => FailedOutput(name, error, ctx)
        case AssertionThrew(AssertionName(name), error) => FailedOutput(name, error.getMessage, Map.empty[String, String])//FIX
      }

      TestOutput(tr.test.name.value, assertionOutputs, testResultToPassable(tr))
    }

    SuiteOutput(suiteResult.suite.name.value, testOutputs, suiteResultToPassable(suiteResult))
  }

  def defaultPrinterSetting(showColours: ColourOutput): PrinterSetting = new PrinterSetting(
    suitePassedToken =  colourise(green(showColours), "[passed]"),
    suiteFailedToken = colourise(red(showColours), "[failed]"),
    testPassedToken = colourise(green(showColours), "[passed]"),
    testFailedToken = colourise(red(showColours), "[failed]"),
    assertionPassedToken = colourise(green(showColours), "[✓]"),
    assertionFailedToken = colourise(red(showColours), "[✗]"),
    testPadding = "",
    assertionPadding = " " * 2,
    assertionFailedPadding = " " * 4,
    assertionFailedContextPadding = " " * 7,
    assertionFailedContextElementPadding = " " * 10

  ) {
    override def colourError(message: String): String = colourise(redU(showColours), message)
  }
}