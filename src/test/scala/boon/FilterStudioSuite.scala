package boon

import syntax.EqSyntax._

final class FilterStudioSuite extends SuiteLike("FilterStudio") {

  import FilterStudio._

  test("remove studio audience") {
    val audienceList = List("myfun", "studio", "balh", "blee")
    val expectedAudience = List("myfun","balh", "blee")
    val actualList = filterStudio(audienceList)
    actualList =?= expectedAudience
  }

  test("with empty audience") {
    filterStudio(Nil) =?= List("studio")
  }

  test("not remove anything else") {
    val audienceList = List("myfun", "balh", "blee")
    val actualList = filterStudio(audienceList)
    actualList =?= audienceList
  }
}
