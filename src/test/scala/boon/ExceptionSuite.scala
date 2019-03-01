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
    ("abcd".toInt =!= Ex(NFE, """For input string: "abcd"""") |
      "Number format error") &
    (List.empty[String].head =!= Ex(NSE, "head of empty list") | "Head on empty List") &
    (new Flakey().blow =!= Ex(RE, "boom!") | "throw RuntimeException") &
    (new Flakey().blowNested =!= Ex(RE, "nested boom!") | "throw nested exception") &
    (new Flakey().safe =!= NotEx(B) | "does not throw")
  }

  private val t2 = test("Alternate syntax-1") {
    ("abcd".toInt =!!= {
      case Ex(cn, msg) =>
        (cn == NFE | "Exception class") & (msg.contains("abcd") | "exception message")
      case NotEx(cn) => failAssertion | s"Expected Exception but got: $cn"
    })
  }

  private val t3 = test("Alternate syntax-2") {
    "abcd".toInt =!!!= (_ == NFE, _.contains("xbcd"))
  }

  override val tests = NonEmptySeq.nes(t1, t2, t3)
}