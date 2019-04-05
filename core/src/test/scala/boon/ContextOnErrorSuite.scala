package boon

import syntax._
import result.SuiteOutput
import result.SequentialPassData
import result.SequentialFailData
import result.SequentialNotRunData
import result.AssertionOutput
import result.Trace
import model.TestState

object ContextOnErrorSuite extends SuiteLike("ContextOnErrorSuite") {

  private val so = ContextOnErrorFixture.run

  private val t1 = test("show context on error") {
    so.tests.length =?= 1 | "have 1 test" and %@(so.tests.head) {
      _.fold(testRan, testThrew, testIgnored)
    } seq()
  }

  private def testIgnored(name: String): ContinueSyntax = {
    fail(s"test ignored: $name") | "testoutput type"
  }

  private def testThrew(name: String, error: String, trac: Seq[Trace], loc: SourceLocation): ContinueSyntax = {
    fail(s"test threw: $name") | "testoutput type"
  }

  private def testRan(name: String, t1assertions: NonEmptySeq[AssertionOutput], state: TestState): ContinueSyntax = {
    t1assertions.length =?= 1                 | "have 1 assertion" and
    state               =?= TestState.Failed  | s"test: $name should have failed" and
    t1assertions.head.fold(assertFailure,
                           assertSuccess,
                           assertSequentialSuccess,
                           assertSequentialFailure)
  }

  private def assertFailure(name: String, errors: NonEmptySeq[String], context: Map[String, String], loc: SourceLocation): ContinueSyntax = {
      name    =?= "Frodo is a hobbit"          | "assertion.name"  and
      errors  =?= one("false is not true")     | "assertion.error" and
      context =?= Map(
                      "allHobbits" -> "Bilbo,Sam,Bingo,Merimas",
                      "missing"    -> "Frodo") | "assertion.context"
  }

  private def assertSuccess(name: String): ContinueSyntax = fail(s"assertion passed : $name") | "assertionOutput type"

  private def assertSequentialSuccess(name: String, passed: NonEmptySeq[SequentialPassData]): ContinueSyntax = {
    fail(s"single assertion passed but expected an assertion: $name") | "assertionOutput type"
  }

  private def assertSequentialFailure(name: String, failed: SequentialFailData, passed: Seq[SequentialPassData], notRun: Seq[SequentialNotRunData]): ContinueSyntax = {
    fail(s"single assertion failed but expected an assertion: $name") | "assertionOutput type"
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

    override val tests = one(frodoTest)
  }

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(withContextSuite))
}