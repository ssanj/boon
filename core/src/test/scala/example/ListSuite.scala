package example

import boon._
import syntax._

object ListSuite extends SuiteLike("ListSuite") {

  private val t1 = test("match contents") {
    List(1, 2, 3, 4, 5) =/= List(2, 4, 6, 8, 10) | "list contents" and
    List(1, 2, 3, 4, 5).length =?= 5             | "list length" and
    //combining assertions for a single object
    %@(List(1, 2, 3, 4, 5)) { list =>
      list.sum =?= 15 | "list sum" and list.take(2) =?= List(1,2) | "list take"
    }
  }

  override val tests = one(t1)
}