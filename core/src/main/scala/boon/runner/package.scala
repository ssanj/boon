package boon

import boon.model.AssertionData
import result.SuiteOutput
import printers.PrinterSetting
import printers.SimplePrinter
import printers.ColourOutput
import model.DeferredSuite
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

  def runAssertion(assertion: AssertionData, moreAssertion: AssertionData*): Unit = {
    val (suiteName, testName) = randomSuiteName
    val suite = new SuiteLike(suiteName) {
      override val tests = oneOrMore(test(testName)(oneOrMore(assertion, moreAssertion:_*)))
    }

    runSuite(suite.suite)
  }

  private def randomSuiteName: (String, String) = suiteNames.get(Random.nextInt(suiteNames.length)).getOrElse(suiteNames.head)

  private def runSuite(suite: DeferredSuite): Unit = {
    val suiteResult   = Boon.runSuite(suite)
    val outputFormat  = SuiteOutput.toSuiteOutput(suiteResult)
    val printSettings = PrinterSetting.defaults(ColourOutput.fromBoolean(true))
    SimplePrinter(outputFormat, printSettings, println)
  }
}

