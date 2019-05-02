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
      "Tig Notaro" -> "My little Suitecase",
      "Dave Chappelle" -> "Grape Drink!"
    )

  def randomSuiteName: (String, String) = suiteNames.get(Random.nextInt(suiteNames.length)).getOrElse(suiteNames.head)

  def runSuite(suite: DeferredSuite): Unit = {
    val suiteResult   = Boon.runSuite(suite)
    val outputFormat  = SuiteOutput.toSuiteOutput(suiteResult)
    val printSettings = PrinterSetting.defaults(ColourOutput.fromBoolean(true))
    SimplePrinter(outputFormat, printSettings, println)
  }

 def runAssertion(assertion: AssertionData): Unit = {
    val (suiteName, testName) = randomSuiteName
    val suite = new SuiteLike(suiteName) {
      override val tests = oneOrMore(test(testName)(assertion))
    }

    runSuite(suite.suite)
 }
}

