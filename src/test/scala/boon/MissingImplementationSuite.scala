package boon

import syntax._
import printers._

object MissingImplementationSuite extends SuiteLike("MissingImplementationSuite") {

  private val t1 = test("can handle missing implementations") {

    val so = MissingImplFixtures.run

    (so.tests.length =?= 1 | "no of tests") &
    (so.tests.head.name =?= "test for missing impl" | "test name") &
    (so.tests.head.assertions.length =?= 1 | "no of assertions") &
    {
      SuiteOutput.assertionFold({ (name, error, _) =>
        (passAssertion | "assertionOutput type") &
        (name =?= "Boolean test" | "assertion name") &
        (error =?= "an implementation is missing" | "assertion error")
      }, _ => failAssertion | "assertionOutput type")(so.tests.head.assertions.head)
    }
  }

  override val tests = NonEmptySeq.nes(t1)
}

object MissingImplFixtures {

  final class SomeClass {
    def predicate: Boolean = ???
  }

  private val missingImplSuite = new SuiteLike("MissingImplementationSuite") {
    val missingImplTest = test("test for missing impl") {
      new SomeClass().predicate =?= true | "Boolean test"
    }

    override val tests = NonEmptySeq.nes(missingImplTest)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(missingImplSuite))
}

