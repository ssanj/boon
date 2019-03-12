package boon

import syntax._
import printers._

object MissingImplementationSuite extends SuiteLike("MissingImplementationSuite") {

  private val t1 = test("can handle missing implementations") {

    val so = MissingImplFixtures.run

    so.tests.length =?= 1 | "no of tests" and
    so.tests.head.name =?= "test for missing impl" | "test name" and
    so.tests.head.assertions.length =?= 3 | "no of assertions" and
    {
      so.tests.head.assertions.head.fold({
        case (name, error, _, Some(loc)) =>
          pass | "assertionOutput type" and
          name =?= "Boolean test" | "assertion name" and
          error =?= "an implementation is missing" | "assertion error" and
          loc.endsWith("MissingImplementationSuite.scala:43") |# ("error location", "loc" -> loc)
        case (name, error, _, None) => fail("Assertion location is missing") | "assertionOutput location missing"
      }, fo => fail(s"Invalid assertion type: $fo") | "assertionOutput type",
         (name, _) => fail(s"composite passed: $name") | "assertionOutput type",
         (name, _, _, _) => fail(s"composite failed: $name") | "assertionOutput type"
      )
    }
  }

  override val tests = NonEmptySeq.nes(t1)
}

object MissingImplFixtures {

  final class SomeClass {
    def predicate: Boolean = ???
    def priority: Int = ???
    def message: String = ???
  }

  private val missingImplSuite = new SuiteLike("MissingImplementationSuite") {
    val missingImplTest = test2("test for missing impl") {
      new SomeClass().predicate | "Boolean test" and /* Tests for a bug with lazy evaluation */
      new SomeClass().priority =?= 10 | "Int test" and
      {
        val message = (new SomeClass().message)
        message =?= "it's a trap" | "Unsafe test"
      }
    }

    override val tests = NonEmptySeq.nes(missingImplTest)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(missingImplSuite))
}

