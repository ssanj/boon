package boon

import boon.model.AssertionData
import boon.model.Predicate
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
  private val suiteNames =
    oneOrMore(
      "Conan O'Brien"  -> "When all else fails there's always delusion",
      "Conan O'Brien"  -> "What is wrong with you right now?",
      "Pete Holmes"    -> "I refer to myself as ‘Old Petey Pants`",
      "Pete Holmes"    -> "Everybody sleeps",
      "Tig Notaro"     -> "My little Suitecase",
      "Tig Notaro"     -> "Oh? She's a fan? Let's give her a ring-a-ding!",
      "Dave Chappelle" -> "Grape Drink!",
      "Dave Chappelle" -> "I plead the fif!",
    )

  private val assertionNames =
    oneOrMore(
      "Like a diamond in the sky",
      "Don’t forget to squeak!",
      "The wipers on the bus go swish, swish, swish",
      "And on his farm he had a cow",
      "Which finger did it bite?",
      "They were neither up nor down",
      "No more monkeys jumping on the bed!",
      "the lamb was sure to go",
      "One for the dame",
      "The clock struck one"
    )

  def runPredicates(predicate: Predicate[_], morePredicates: Predicate[_]*)(implicit config: ReplConfig): Unit = {
    runAssertions(predicate | randomAssertionName, morePredicates.map(p => p | randomAssertionName):_*)
  }

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

  private def randomAssertionName: String = assertionNames.get(Random.nextInt(assertionNames.length)).getOrElse(assertionNames.head)
}

