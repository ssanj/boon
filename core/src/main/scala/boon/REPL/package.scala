package boon

import boon.model.AssertionData
import boon.model.Test
import result.SuiteOutput
import printers.SimplePrinter
import printers.BoonPrinter
import printers.ColourOutput
import scala.util.Random

final case class ReplConfig(printer: BoonPrinter)

object ReplConfig {
  implicit val defaultConfig: ReplConfig = ReplConfig(SimplePrinter)
}

package object REPL {
  //load this in from a file
  val suiteNames =
    oneOrMore(
      "Conan O'Brien"  -> "When all else fails there's always delusion",
      "Conan O'Brien"  -> "What is wrong with your right now?",
      "Pete Holmes"    -> "I refer to myself as ‘Old Petey Pants`",
      "Pete Holmes"    -> "Everybody sleeps",
      "Tig Notaro"     -> "My little Suitecase",
      "Tig Notaro"     -> "Oh? She's a fan? Let's give her a ring-a-ding!",
      "Dave Chappelle" -> "Grape Drink!",
      "Dave Chappelle" -> "I plead the fif!",
    )

  def runAssertions(assertion: AssertionData, moreAssertion: AssertionData*)(implicit config: ReplConfig): Unit = {
    val (suiteName, testName) = randomSuiteAndTestName
    val suite = new SuiteLike(suiteName) {
      override val tests = oneOrMore(test(testName)(oneOrMore(assertion, moreAssertion:_*)))
    }

    runSuites(suite)(config)
  }

  def runTests(test: Test, moreTests: Test*)(implicit config: ReplConfig): Unit = {
    val suiteName = randomSuiteName

    val suite = new SuiteLike(suiteName) {
      override val tests = oneOrMore(test, moreTests:_*)
    }

    runSuites(suite)(config)
  }

  def runSuites(suite: SuiteLike, moreSuites: SuiteLike*)(implicit config: ReplConfig): Unit = {
    oneOrMore(suite, moreSuites:_*).foreach(runSingleSuite(_)(config))
  }

  def runSingleSuite(suite: SuiteLike)(implicit config: ReplConfig): Unit = {
    val suiteResult   = Boon.runSuiteLike(suite)
    val outputFormat  = SuiteOutput.toSuiteOutput(suiteResult)
    config.printer.print(ColourOutput.fromBoolean(true), println, outputFormat)
  }

  private def randomSuiteAndTestName: (String, String) = suiteNames.get(Random.nextInt(suiteNames.length)).getOrElse(suiteNames.head)

  private def randomSuiteName: String = suiteNames.get(Random.nextInt(suiteNames.length)).getOrElse(suiteNames.head)._1
}
