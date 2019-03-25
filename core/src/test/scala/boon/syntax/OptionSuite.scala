package example

import boon._
import syntax._
import option._

object OptionSuite extends SuiteLike("OptionSuite") {

  private val t1 = test("some") {
    10.some =?= 10.some   | "equal Some"    and
    10.some =/= 19.some   | "unequal Some"  and
    50.some =/= none[Int] | "Some != None"  and
    some_?[Int](_ =/= 10  | "Some is ten")(20.some)
  }

  private val t2 = test("none") {
    none[Int]     =?= none[Int] | "equal None"    and
    none[Int]     =/= 10.some   | "None != Some"  and
    none_?[Int](none[Int])
  }

  override val tests = oneOrMore(t1, t2)
}