package boon

import syntax._

abstract class ToBeImplementedSuite extends SuiteLike("ToBeImplementedSuite") {

  final class SomeClass {
    def predicate: Boolean = ???
    def priority: Int = ???
    def message: String = ???
  }

  val t1 = test("test for missing implementations") {
    new SomeClass().predicate | "Boolean test" and /* Tests for a bug with lazy evaluation */
    new SomeClass().priority =?= 10 | "Int test" and
    %@(new SomeClass().message) { message =>
      message =?= "it's a trap" | "Unsafe test"
    }
  }

  override val tests = NonEmptySeq.nes(t1)
}