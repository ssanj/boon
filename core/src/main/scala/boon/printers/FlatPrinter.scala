package boon
package printers

import boon.result.AssertionOutput
import boon.result.Trace
import boon.data.NonEmptySeq
import boon.result.TestOutput
import boon.model.TestState
import boon.model.SuiteState
import boon.result.SuiteOutput
import scala.compat.Platform.EOL
import Colourise.green
import Colourise.red

//prints out only SuiteName / TestName
object FlatPrinter extends BoonPrinter {

  override def print(co: ColourOutput, output: String => Unit, suiteOutput: SuiteOutput): Unit ={
    output(suiteOutputString(suiteOutput, co))
  }

  private def suiteOutputString(suiteOutput: SuiteOutput, co: ColourOutput): String = suiteOutput match {
    case SuiteOutput(name, tests, state) =>
      val suiteName = state match {
        case SuiteState.Passed => Colourise.colourise(green(co), name)
        case SuiteState.Failed => Colourise.colourise(red(co), name)
      }

      tests.map(testOutputString(_, suiteName, co)).mkString(EOL)
  }

  private def testOutputString(to: TestOutput, suiteName: String, co: ColourOutput): String = {
    to.fold[String](testPassed(co, suiteName), testThrew(co, suiteName), testIgnored(suiteName))
  }

  private def testPassed(co: ColourOutput, pContext: String)(name: String, assertions: NonEmptySeq[AssertionOutput], state: TestState): String = {
    val testName = state match {
      case TestState.Passed  => Colourise.colourise(green(co), name)
      case TestState.Failed  => Colourise.colourise(red(co), name)
      case TestState.Ignored => s"?${name}"
    }

    s"${pContext} / ${testName}"
  }

  private def testThrew(co: ColourOutput, pContext: String)(name: String, error: String, trace: Seq[Trace], loc: SourceLocation): String = {
    val testName = Colourise.colourise(red(co), name)
    s"${pContext} / ${testName}"
  }

  private def testIgnored(pContext: String)(name: String): String = {
    val testName = name
    s"${pContext} / ${testName}"
  }
}

