package boon

import syntax._

object ExceptionSuite extends SuiteLike("ExceptionSuite") {

  private class Flakey() {
    def blow: Int = throw new RuntimeException("boom!")
    def blowNested: String = throw new RuntimeException("nested boom!", new IllegalArgumentException("This is illegal"))
    def safe: Boolean = true
  }

  private val NFE = "java.lang.NumberFormatException"
  private val NSE = "java.util.NoSuchElementException"
  private val RE = "java.lang.RuntimeException"
  private val B = "java.lang.Boolean"

  private val t1 = test("Exception syntax") {
    "abcd".toInt =!= Ex(NFE, """For input string: "abcd"""") |
      "Number format error" and
    List.empty[String].head =!= Ex(NSE, "head of empty list") | "Head on empty List" and
    new Flakey().blow =!= Ex(RE, "boom!") | "throw RuntimeException" and
    new Flakey().blowNested =!= Ex(RE, "nested boom!") | "throw nested exception" and
    new Flakey().safe =!= NotEx(B) | "does not throw"
  }

  private val t2 = test("Alt Exception syntax") {
    new Flakey().blowNested =!!= { bex =>
      bex.className =?= RE | "nested.exception.className" and
      bex.message.contains("boom!") | "nested.exception.message"
    }
  }

  override val tests = NonEmptySeq.nes(t1, t2)
}