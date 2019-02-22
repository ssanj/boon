package boon

import syntax._
import example.FilterStudio
import FilterStudio._
import Boon.test

object FilterStudioSuite extends SuiteLike("FilterStudio") {
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

object FilterStudioSuiteRunner {

  def main(args: Array[String]): Unit =  {
    val suiteResult = Boon.runSuiteLike(FilterStudioSuite)
    val suiteOutput = SuiteOutput.toSuiteOutput(suiteResult)
    println(SimplePrinter.print(suiteOutput, SuiteOutput.defaultPrinterSetting(true)))
  }
}
