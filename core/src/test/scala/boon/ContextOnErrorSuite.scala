package boon

import result.SuiteOutput
import result.SequentialPassData
import result.SequentialFailData
import result.SequentialNotRunData
import result.AssertionOutput
import result.Trace
import data.NonEmptySeq
import model.TestState
import model.AssertionData
import internal.instances._

object ContextOnErrorSuite extends SuiteLike("ContextOnErrorSuite") {

  private val so = ContextOnErrorFixture.run

  private val t1 = test("show context on error") {
    sequentially(so.tests.length =?= 1 | "have 1 test" and %@(so.tests.head) {
      _.fold(testRan, testThrew, testIgnored)
    })
  }

  private def testIgnored(name: String): AssertionData = {
    fail(s"test ignored: $name") | "testoutput type"
  }

  private def testThrew(name: String, error: String, trac: Seq[Trace], loc: SourceLocation): AssertionData = {
    fail(s"test threw: $name") | "testoutput type"
  }

  private def testRan(name: String, t1assertions: NonEmptySeq[AssertionOutput], state: TestState): AssertionData = {
    t1assertions.length =?= 1                 | "have 1 assertion" and
    state               =?= TestState.Failed  | s"test: $name should have failed" and
    t1assertions.head.fold(assertFailure,
                           assertSuccess,
                           assertSequentialSuccess,
                           assertSequentialFailure)
  }

  private def assertFailure(name: String, errors: NonEmptySeq[String], context: Map[String, String], loc: SourceLocation): AssertionData = {
      name    =?= "Frodo is a hobbit"      | "assertion.name"  and
      errors  =?= one("false != true")     | "assertion.error" and
      context =?= Map(
                      "allHobbits" -> "Bilbo,Sam,Bingo,Merimas",
                      "missing"    -> "Frodo") | "assertion.context"
  }

  private def assertSuccess(name: String): AssertionData = fail(s"assertion passed : $name") | "assertionOutput type"

  private def assertSequentialSuccess(name: String, passed: NonEmptySeq[SequentialPassData]): AssertionData = {
    fail(s"single assertion passed but expected an assertion: $name") | "assertionOutput type"
  }

  private def assertSequentialFailure(name: String, failed: SequentialFailData, passed: Seq[SequentialPassData], notRun: Seq[SequentialNotRunData]): AssertionData = {
    fail(s"single assertion failed but expected an assertion: $name") | "assertionOutput type"
  }

  override val tests = one(t1)
}

object ContextOnErrorFixture {

  private val withContextSuite = new SuiteLike("WithContextSuite") {
    val frodoTest = test("LOTR") {
      val hobbits = List("Bilbo", "Sam", "Bingo", "Merimas")
      hobbits.contains("Frodo") | ("Frodo is a hobbit",
                                   "allHobbits" -> hobbits.mkString(","),
                                   "missing"    -> "Frodo")
    }

    override val tests = one(frodoTest)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(withContextSuite))
}