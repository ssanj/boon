package boon

import syntax.EqSyntax._
import example.FilterStudio
import FilterStudio._
import Boon.test

object FilterStudioTests {
  val t1 = test("with empty audience") {
    (filterStudio(Nil) =?= List("studio"))
  }

  val t2 = test("remove studio audience") {
      val audienceList = List("myfun", "studio", "balh", "blee")
      val expectedAudience = List("myfun","balh", "blee")
      val actualList = filterStudio(audienceList)
      actualList =?= expectedAudience
    }

  val t3 = test("not remove anything else") {
    val audienceList = List("myfun", "balh", "blee")
    val actualList = filterStudio(audienceList)
    actualList =?= audienceList
  }
}

import FilterStudioTests._

final class FilterStudioSuite extends SuiteLike("FilterStudio")(NonEmptySeq.nes(t1, t2, t3))

object FilterStudioSuite {

  def main(args: Array[String]): Unit =  {
    val s1 = new FilterStudioSuite
    println(Printer.suiteResultOutput(Boon.runSuiteLike(s1)))
  }
}
