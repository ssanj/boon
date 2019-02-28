package boon

import syntax._

object ExceptionSuite extends SuiteLike("ExceptionSuite") {

  private class Flakey() {
    def blow: Int = throw new RuntimeException("boom!")
    def blowNested: String = throw new RuntimeException("nested boom!", new IllegalArgumentException("This is illegal"))
    def safe: Boolean = true
  }

  private val t1 = test("Exception syntax") {
    ("abcd".toInt =!=
      Ex("java.lang.NumberFormatException", """For input string: "abcd"""") |
      "Number format error") &
    (List.empty[String].head =!=
      Ex("java.util.NoSuchElementException", "head of empty list") |
      "Head on empty List") &
    (new Flakey().blow =!= Ex("java.lang.RuntimeException", "boom!") | "throw RuntimeException") &
    (new Flakey().blowNested =!= Ex("java.lang.RuntimeException", "nested boom!") | "throw nested exception") &
    (new Flakey().safe =!= NotEx("java.lang.Boolean") | "does not throw")
  }

  override val tests = NonEmptySeq.nes(t1)
}