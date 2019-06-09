package boon
package syntax

import option._

object OptionSuite extends SuiteLike("OptionSuite") {

  private val t1 = test("some") {
    10.some =?= 10.some   | "equal Some"                  and
    10.some =/= 19.some   | "unequal Some"                and
    50.some =/= none[Int] | "Some != None"                and
    some_?[Int](20.some)(_ =/= 10  | "twenty is not ten") and
    some_?[String]("value".some)(_ => pass | "is Some")   and
    isSome(30.some)                                       and
    isSome("Metals".some)
  }

  private val t2 = test("none") {
    none[Int]     =?= none[Int] | "equal None"     and
    none[Int]     =/= 10.some   | "None != Some"   and
    none_?[Int](none[Int])(pass | "expected None") and
    isNone[String](none[String])
  }

  override val tests = oneOrMore(t1, t2)
}