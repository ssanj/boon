package boon
package model

object DifferenceSuite extends SuiteLike("Difference Suite") {

  private val t1 = test("difference") {
    val intDiff    = Difference[Int]
    val stringDiff = Difference[String]

    intDiff.diff(5, 5, IsEqual)    =?= one("5 != 5") | "Int equal difference"                                   and
    intDiff.diff(5, 5, IsNotEqual) =?= one("5 == 5") | "Int not equal difference"                               and
    stringDiff.diff("Hello", "Hello", IsEqual) =?= one("\"Hello\" != \"Hello\"") | "String equal difference"    and
    stringDiff.diff("Hello", "Hello", IsNotEqual) =?= one("\"Hello\" == \"Hello\"") | "String not equal difference" 
  }

  override val tests = oneOrMore(t1)
}