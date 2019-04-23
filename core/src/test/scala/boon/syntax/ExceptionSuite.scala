package boon
package syntax

import exception._

object ExceptionSuite extends SuiteLike("Exception Syntax Suite") {

  private val t1 = test("Exception Thrown: NumberFormatException") {
    "abcd".toInt =!=[NumberFormatException](_ =?= """For input string: "abcd"""" |
      "number format error")
  }

  private val t2 = test("Exception Thrown: NoSuchElementException") {
    List.empty[String].head =!=[NoSuchElementException](_ =?= "head of empty list" |
      "head on empty List")
  }

  private val t3 = test("Exception Instance") {
    val ex = new UnsupportedOperationException("blurb")
    ex =!=[UnsupportedOperationException](_ =?= "blurb" | "exception instance")
  }

  override val tests = oneOrMore(t1, t2, t3)
}