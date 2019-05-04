package boon

import boon.model.AssertionData
import boon.model.Test
import result.SuiteOutput
import printers.PrinterSetting
import printers.SimplePrinter
import printers.ColourOutput
import scala.util.Random

package object runner {

  //load this in from a file
  val suiteNames =
    oneOrMore(
      "Conan O'Brien" -> "When all else fails there's always delusion",
      "Pete Holmes" -> "I refer to myself as ‘Old Petey Pants`",
      "Pete Holmes" -> "Everybody sleeps",
      "Tig Notaro" -> "My little Suitecase",
      "Tig Notaro" -> "Oh? She's a fan? Let's give her a ring-a-ding!",
      "Dave Chappelle" -> "Grape Drink!",
      "Dave Chappelle" -> "I plead the fif!",
    )

  def runAssertions(assertion: AssertionData, moreAssertion: AssertionData*): Unit = {
    val (suiteName, testName) = randomSuiteAndTestName
    val suite = new SuiteLike(suiteName) {
      override val tests = oneOrMore(test(testName)(oneOrMore(assertion, moreAssertion:_*)))
    }

    runSuites(suite)
  }

  def runTests(test: Test, moreTests: Test*): Unit = {
    val suiteName = randomSuiteName

    val suite = new SuiteLike(suiteName) {
      override val tests = oneOrMore(test, moreTests:_*)
    }

    runSuites(suite)
  }

  def runSuites(suite: SuiteLike): Unit = {
    val suiteResult   = Boon.runSuiteLike(suite)
    val outputFormat  = SuiteOutput.toSuiteOutput(suiteResult)
    val printSettings = PrinterSetting.defaults(ColourOutput.fromBoolean(true))
    SimplePrinter(outputFormat, printSettings, println)
  }

  private def randomSuiteAndTestName: (String, String) = suiteNames.get(Random.nextInt(suiteNames.length)).getOrElse(suiteNames.head)

  private def randomSuiteName: String = suiteNames.get(Random.nextInt(suiteNames.length)).getOrElse(suiteNames.head)._1
}

