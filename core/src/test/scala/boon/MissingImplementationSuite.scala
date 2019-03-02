package boon

import syntax._
import printers._

object MissingImplementationSuite extends SuiteLike("MissingImplementationSuite") {

  private val t1 = test("can handle missing implementations") {

    val so = MissingImplFixtures.run

    (so.tests.length =?= 1 | "no of tests") &
    (so.tests.head.name =?= "test for missing impl" | "test name") &
    (so.tests.head.assertions.length =?= 2 | "no of assertions") &
    {
      so.tests.head.assertions.head.fold({
        case (name, error, _, Some(loc)) =>
          (passAssertion | "assertionOutput type") &
          (name =?= "Boolean test" | "assertion name") &
          (error =?= "an implementation is missing" | "assertion error") &
          (loc.endsWith("MissingImplementationSuite.scala:39") |# ("error location", "loc" -> loc))
        case (name, error, _, None) => failAssertion | "assertionOutput location missing"
      }, _ => failAssertion | "assertionOutput type")
    }
  }

  override val tests = NonEmptySeq.nes(t1)
}

object MissingImplFixtures {

  final class SomeClass {
    def predicate: Boolean = ???
    def priority: Int = ???
  }

  private val missingImplSuite = new SuiteLike("MissingImplementationSuite") {
    val missingImplTest = test("test for missing impl") {
      (new SomeClass().predicate | "Boolean test") & /* Tests for a bug with lazy evaluation */
      (new SomeClass().priority =?= 10 | "Int test")
    }

    override val tests = NonEmptySeq.nes(missingImplTest)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(missingImplSuite))
}

