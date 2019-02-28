package boon

import syntax._
import printers.SuiteOutput

object ContextOnErrorSuite extends SuiteLike("ContextOnErrorSuite") {

  val so = ContextOnErrorFixture.run

  private val t1 = test("show context on error") {
    (so.tests.length =?= 1 | "have 1 test") &
    (so.tests.toSeq(0).assertions.length =?= 1 | "have 1 assertion") &
    so.tests.toSeq(0).assertions.toSeq(0).fold({(name, error, context) =>
      (name =?= "Frodo is a hobbit" | "assertion.name") &
      (error =?= """false is not true""" | "assertion.error") &
      (context =?= Map("allHobbits" -> "Bilbo,Sam,Bingo,Merimas", "missing" -> "Frodo") | "assertion.context")
    }, _ => failAssertion | "assertionType")
  }

  override val tests = NonEmptySeq.nes(t1)
}

object ContextOnErrorFixture {

  private val withContextSuite = new SuiteLike("WithContextSuite") {
    val frodoTest = test("LOTR") {
      val hobbits = List("Bilbo", "Sam", "Bingo", "Merimas")
      hobbits.contains("Frodo") |# ("Frodo is a hobbit",
                                    "allHobbits" -> hobbits.mkString(","),
                                    "missing" -> "Frodo")
    }

    override val tests = NonEmptySeq.nes(frodoTest)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(withContextSuite))
}