package boon

import syntax._
import result.SuiteOutput
import result.SequentialPassData
import result.SequentialFailData
import result.SequentialNotRunData
import result.AssertionOutput
import result.Trace
import model.TestState

object MissingImplementationSuite extends SuiteLike("MissingImplementationSuite") {

  private val t1 = test("can handle missing implementations") {
      val so    = MissingImplFixtures.run
      val tests = so.tests

      tests.length =?= 1 | "no of tests" and %@(tests.head) {
        _.fold(testRan, testThrew, testIgnored)
      } seq()
  }

  private def testRan(name: String, t1assertions: NonEmptySeq[AssertionOutput], state: TestState): ContinueSyntax = {
    val assertions = t1assertions.toSeq

    name =?= "test for missing implementations" | "test name" and
    assertions.length =?= 3 | "no of assertions" and
    state =?= TestState.Failed | "test failed" and %@(assertions(0)) {
      asserNotImplementedTest("Boolean test", 16)
    } and %@(assertions(1)) {
      asserNotImplementedTest("Int test", 17)
    } and %@(assertions(2)) {
      asserNotImplementedTest("Unsafe test", 18)
    }
  }

  private def asserNotImplementedTest(expectedName: String, expectedLoc: Int)(ao: AssertionOutput): ContinueSyntax = {
    ao.fold(assertionFailed(expectedName, expectedLoc),
            assertionPassed,
            sequentialAssertionPassed,
            sequentialAssertionFailed)
  }

  private def testThrew(name: String, error: String, trac: Seq[Trace], loc: SourceLocation): ContinueSyntax =
    fail(s"thrown test: $name") | "test type"

  private def testIgnored(name: String): ContinueSyntax = fail(s"ignored test: $name") | "testType"

  private def assertionFailed(expectedName: String, expectedLoc: Int)(name: String, error: String, context: Map[String, String], loc: SourceLocation): ContinueSyntax = {
    pass | s"${expectedName}.assertionOutput type" and
    name =?= expectedName | s"${expectedName}.assertion name" and
    error =?= "an implementation is missing" | s"${expectedName}.assertion error" and
    SuiteOutput.sourceLocation(loc).fold(
      fail("expected SourceLocation") | s"${expectedName}.error location"
    )(loc => loc.endsWith(s"ToBeImplementedSuite.scala:${expectedLoc}") |# (s"${expectedName}.error location", s"${expectedName}.loc" -> loc))
  }

  private def assertionPassed(name: String): ContinueSyntax = {
    fail(s"passed: $name") | "assertionOutput type",
  }

  private def sequentialAssertionPassed(name: String, passed: NonEmptySeq[SequentialPassData]): ContinueSyntax = {
    fail(s"Sequential passed: $name") | "assertionOutput type"
  }

  private def sequentialAssertionFailed(name: String, failed: SequentialFailData, passed: Seq[SequentialPassData], notRun: Seq[SequentialNotRunData]): ContinueSyntax = {
    fail(s"Sequential failed: $name") | "assertionOutput type"
  }

  override val tests = NonEmptySeq.nes(t1)
}

object MissingImplFixtures {

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(new ToBeImplementedSuite{}))
}

