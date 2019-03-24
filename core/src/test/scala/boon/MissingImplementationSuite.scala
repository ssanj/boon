package boon

import syntax._
import result.SuiteOutput
import result.SequentialPassData
import result.SequentialFailData
import result.SequentialNotRunData
import result.AssertionOutput
import model.Passable
import result.Trace
import model.Failed

object MissingImplementationSuite extends SuiteLike("MissingImplementationSuite") {

  private val t1 = test("can handle missing implementations") {
      val so    = MissingImplFixtures.run
      val tests = so.tests

      tests.length =?= 1 | "no of tests" and %@(tests.head) {
        _.fold(testRan, testThrew, testIgnored)
      } seq()
  }

  private def testRan(name: String, t1assertions: NonEmptySeq[AssertionOutput], passable: Passable): ContinueSyntax = {
    val assertions = t1assertions.toSeq

    name =?= "test for missing implementations" | "test name" and
    assertions.length =?= 3 | "no of assertions" and
    passable =?= Failed | "test failed" and %@(assertions(0)) {
      _.fold(assertionFailed("Boolean test", 16),
             assertionPassed,
             sequentialAssertionPassed,
             sequentialAssertionFailed)
    } and %@(assertions(1)) {
      _.fold(assertionFailed("Int test", 17),
             assertionPassed,
             sequentialAssertionPassed,
             sequentialAssertionFailed)
    } and %@(assertions(2)) {
      _.fold(assertionFailed("Unsafe test", 18),
             assertionPassed,
             sequentialAssertionPassed,
             sequentialAssertionFailed)
    }
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

