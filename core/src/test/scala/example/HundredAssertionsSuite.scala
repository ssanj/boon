package example

import boon._
import syntax._

object HundredAssertionsSuite extends SuiteLike("HundredAssertionsSuite") {

  private def mkAssertion(index: Int) = {
    if (index % 2 == 0) true =?= true | s"a truism ${index}"
    else false =/= true | s"a falsism ${index}"
  }

  private val t1 = test("A hundred assertions") {
    oneOrMore(1, 2 to 100:_*).map(mkAssertion)
  }

  override val tests = one(t1)
}