package boon

import syntax._
import result.SuiteOutput
import model.Failed
import model.Passable

object ContextOnErrorSuite extends SuiteLike("ContextOnErrorSuite") {

  private val so = ContextOnErrorFixture.run

  implicit val passableBoonType = BoonType.defaults[Passable]

  private val t1 = test("show context on error") {
    so.tests.length =?= 1 | "have 1 test" and %@(so.tests.head) { t1 =>
      t1.fold({ (name, t1assertions, passable) =>
        t1assertions.length =?= 1       | "have 1 assertion" and
        passable            =?= Failed  | s"test: $name should have failed" and
        t1assertions.head.fold({(name, error, context, _) =>
          name    =?= "Frodo is a hobbit"          | "assertion.name"  and
          error   =?= "false is not true"          | "assertion.error" and
          context =?= Map(
                          "allHobbits" -> "Bilbo,Sam,Bingo,Merimas",
                          "missing"    -> "Frodo") | "assertion.context"
        }, fo => fail(s"assertion passed : $fo") | "assertionOutput type",
           (name, _)       => fail(s"single assertion passed but expected an assertion: $name") | "assertionOutput type",
           (name, _, _, _) => fail(s"single assertion failed but expected an assertion: $name") | "assertionOutput type"
        )
      }, (name, _, _, _) => fail(s"test threw: $name") | "testoutput type",
         name => fail(s"test ignored: $name")          | "testoutput type")
    } seq()
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