package boon

import syntax._
import printers._

object SuiteLikeSuite extends SuiteLike("SuiteLike") {

  val t1 = test("can handle missing implementations") {

    val so = SuiteLikeSuiteFixtures.run

    (so.tests.length =?= 1 | "no of tests") &
    (so.tests.head.name =?= "test for missing impl" | "test name") &
    (so.tests.head.assertions.length =?= 1 | "no of assertions") &
    {
      so.tests.head.assertions.head match {
        case FailedOutput(name, error, _) =>
          (passAssertion | "assertionOutput type") &
          (name =?= "Boolean test" | "assertion name") &
          (error =?= "an implementation is missing" | "assertion error")
        case _ => failAssertion | "assertionOutput type"
      }
    }
  }

  override val tests = NonEmptySeq.nes(t1)
}

object SuiteLikeSuiteFixtures {

  final class SomeClass {
    def predicate: Boolean = ???
  }

  private val missingImplSuite = new SuiteLike("Missing Implementation") {
    val missingImplTest = test("test for missing impl") {
      new SomeClass().predicate =?= true | "Boolean test"
    }

    override val tests = NonEmptySeq.nes(missingImplTest)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(missingImplSuite))
}

