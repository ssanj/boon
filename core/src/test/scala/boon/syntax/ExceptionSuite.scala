package boon
package syntax

import exception._

object ExceptionSuite2 extends SuiteLike("ExceptionSuite2") {

  private class Flakey() {
    def blow: Int = throw new RuntimeException("boom!")
    def blowNested: String = throw new RuntimeException("nested boom!", new IllegalArgumentException("This is illegal"))
    def safe: Boolean = true
  }

  private val t1 = test("NumberFormatException") {
    "abcd".toInt =!=[NumberFormatException](_ =?= """For input string: "abcd"""" |
      "number format error")
  }

  private val t2 = test("NoSuchElementException") {
    List.empty[String].head =!=[NoSuchElementException](_ =?= "head of empty list" |
      "head on empty List")
  }

  private val t3 = test("Flakey") {
    val flakey = new Flakey()

    flakey.blow  =!=[RuntimeException](_ =?=  "boom!" |
      "throw RuntimeException") and
    flakey.blowNested =!=[RuntimeException](_.contains("boom!") |
      "nested.exception.message") and
    flakey.safe | "safe does not throw"
  }

  override val tests = oneOrMore(t1, t2, t3)
}