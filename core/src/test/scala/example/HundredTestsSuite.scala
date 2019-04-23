package example

import boon._

object HundredTestsSuite extends SuiteLike("HundredTestsSuite") {

  private def mkTest(index: Int) = test(s"Test$index") {
    if (index % 2 == 0) true =?= true | "a truism"
    else false =/= true | "a falsism"
  }

  override val tests = oneOrMore(1, 2 to 100:_*).map(mkTest)
}