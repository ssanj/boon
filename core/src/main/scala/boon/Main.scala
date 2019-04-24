package boon

import result.SuiteOutput
import printers.PrinterSetting
import printers.SimplePrinter
import printers.ColourOutput

//Lets you run your test as a executable
trait Main { self: SuiteLike =>

  def main(args: Array[String]): Unit = {
    val suiteResult   = Boon.runSuite(suite)
    val outputFormat  = SuiteOutput.toSuiteOutput(suiteResult)
    val printSettings = PrinterSetting.defaults(ColourOutput.fromBoolean(true))
    SimplePrinter(outputFormat, printSettings, println)
  }
}