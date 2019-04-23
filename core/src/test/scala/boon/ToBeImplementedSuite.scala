package boon

abstract class ToBeImplementedSuite extends SuiteLike("ToBeImplementedSuite") {

  final class SomeClass {
    def predicate: Boolean = ???
    def priority: Int = ???
    def message: String = ???
  }

  val t1 = test("test for missing implementations") {
    val someClass = new SomeClass()

    someClass.predicate                  | "Boolean test" and /* Tests for a bug with lazy evaluation */
    someClass.priority =?= 10            | "Int test"     and
    someClass.message  =?= "it's a trap" | "Unsafe test"
  }

  override val tests = one(t1)
}