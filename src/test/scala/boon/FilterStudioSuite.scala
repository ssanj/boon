package boon

import example.FilterStudio

import syntax.EqSyntax._

final class FilterStudioSuite extends SuiteLike("FilterStudio") {

  import FilterStudio._

  override def primary = Boon.defineTest[List[String]]("with empty audience",
    (filterStudio(Nil), List("studio"))
  )

  test("remove studio audience") {
    val audienceList = List("myfun", "studio", "balh", "blee")
    val expectedAudience = List("myfun","balh", "blee")
    val actualList = filterStudio(audienceList)
    actualList =?= expectedAudience
  }

  test("not remove anything else") {
    val audienceList = List("myfun", "balh", "blee")
    val actualList = filterStudio(audienceList)
    actualList =?= audienceList
  }
}

object FilterStudioSuite {

  def main(args: Array[String]): Unit =  {
    val s1 = new FilterStudioSuite
    println(Printer.suiteResultOutput(Boon.runSuiteLike(s1)))
  }
}
