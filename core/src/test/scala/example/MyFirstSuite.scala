package example

import boon._

object MyFirstSuite extends SuiteLike("Simple Stuff") with Main {

  private val t1 = test("equality of things") {
    1 =?= 1             | "Int equality"    and
    "Hello" =?= "Hello" | "String equality" and
    true =?= true       | "Boolean equality"
  }

  override def tests = oneOrMore(t1)
}