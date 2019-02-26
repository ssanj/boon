package boon

import syntax._

import printers.PrinterSetting
import printers.SimplePrinter
import printers.ShowColours
import printers.SuiteOutput

object FilterStudioSuite extends SuiteLike("FilterStudio") {
  import example.FilterStudio._

  private [boon] val t1 = test("with empty abode") {
    (filterStudio(Nil) =?= List("studio") | "stays empty") &
    (filterStudio(Nil) =?= Nil | "more")
  }

  private [boon] val t2 = test("remove studio abode") {
    val abode = List("loft", "studio", "balh", "blee")
    val expectedAbode = List("loft","balh", "blee")
    val actualList = filterStudio(abode)
    actualList =?= expectedAbode | "only remove studio"
  }

  private [boon] val t3 = test("not remove anything else") {
    val abode = List("loft", "balh", "blee")
    val actualList = filterStudio(abode)
    actualList =?= abode | "leave others unchanged"
  }

  override def tests = NonEmptySeq.nes(t1, t2, t3)
}

//To run outside of SBT
object FilterStudioSuiteRunner {

  def main(args: Array[String]): Unit =  {
    val suiteResult = Boon.runSuiteLike(FilterStudioSuite)
    val suiteOutput = SuiteOutput.toSuiteOutput(suiteResult)
    SimplePrinter(suiteOutput, PrinterSetting.defaults(ShowColours), println)
  }
}
