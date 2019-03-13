package boon

import syntax._
import printers.SuiteOutput

object ContextOnErrorSuite extends SuiteLike("ContextOnErrorSuite") {

  private val so = ContextOnErrorFixture.run

  private val t1 = test("show context on error") {
    so.tests.length =?= 1 | "have 1 test" and
    so.tests.toSeq(0).assertions.length =?= 1 | "have 1 assertion" and
    so.tests.toSeq(0).assertions.toSeq(0).fold({(name, error, context, _) =>
      name    =?= "Frodo is a hobbit" | "assertion.name"  and
      error   =?= "false is not true" | "assertion.error" and
      context =?= Map("allHobbits" -> "Bilbo,Sam,Bingo,Merimas", "missing" -> "Frodo") | "assertion.context"
    }, fo => fail(s"assertion passed: $fo") | "assertionOutput",
       (name, _) => fail(s"assertion passed: $name") | "assertionOutput type",
       (name, _, _, _) => fail(s"assertion failed: $name") | "assertionOutput type"
    ) sequentially()
  }

  override val tests = NonEmptySeq.nes(t1)
}

object ContextOnErrorFixture {

  private val withContextSuite = new SuiteLike("WithContextSuite") {
    val frodoTest = test("LOTR") {
      val hobbits = List("Bilbo", "Sam", "Bingo", "Merimas")
      hobbits.contains("Frodo") |# ("Frodo is a hobbit",
                                    "allHobbits" -> hobbits.mkString(","),
                                    "missing"    -> "Frodo")
    }

    override val tests = NonEmptySeq.nes(frodoTest)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(withContextSuite))
}