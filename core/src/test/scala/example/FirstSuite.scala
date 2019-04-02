package example

import boon._
import syntax._

object FirstSuite extends SuiteLike("FirstSuite") {

  private val t1 = test("String methods") {

    "Hello" + " " + "World" =?= "Hello World"    | "concat"    and
    "yohoho"                =?= "ohohoy".reverse | "reversing"
  }

  override def tests = one(t1)
}