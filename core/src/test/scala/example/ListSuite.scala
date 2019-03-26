package example

import boon._
import syntax._

object ListSuite extends SuiteLike("ListSuite") {

  private val t1 = test("match contents") {
    List(1, 2, 3, 4, 5) =/= List(2, 4, 6, 8, 10)               | "list contents" and
    List(1, 2, 3, 4, 5).length =?= List(2, 4, 6, 8, 10).length | "list length"
  }

  override val tests = one(t1)
}