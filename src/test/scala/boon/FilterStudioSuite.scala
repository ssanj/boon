package boon

import syntax._
import example.FilterStudio
import FilterStudio._
import Boon.test

object FilterStudioTests {
  val t1 = test("with empty audience") {
    (filterStudio(Nil) =?= List("studio") | "stays empty") &
    (filterStudio(Nil) =?= List("") | "more")
  }

  val t2 = test("remove studio audience") {
      val audienceList = List("myfun", "studio", "balh", "blee")
      val expectedAudience = List("myfun","balh", "blee")
      val actualList = filterStudio(audienceList)
      actualList =?= expectedAudience | "only remove studio"
    }

  val t3 = test("not remove anything else") {
    val audienceList = List("myfun", "balh", "blee")
    val actualList = filterStudio(audienceList)
    actualList =?= audienceList | "leave others unchanged"
  }
}

import FilterStudioTests._

final class FilterStudioSuite extends SuiteLike("FilterStudio")(t1, t2, t3)

object FilterStudioSuite {

  def main(args: Array[String]): Unit =  {
    val s1 = new FilterStudioSuite
    println(Printer.suiteResultOutput(Boon.runSuiteLike(s1)))
  }
}
