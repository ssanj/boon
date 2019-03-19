package example

import boon._
import syntax._

object FirstSuite extends SuiteLike("FirstSuite") {

  private val t1 = test("String methods") {

    "Hello" + " " + "World" =?= "Hello World"    | "concat"    and
    "yohoho"                =?= "ohohoy".reverse | "reversing" and
    % {
      val x = null.toString
      true | s"$x is null"
    }
  }

  override def tests = NonEmptySeq.nes(t1)
}