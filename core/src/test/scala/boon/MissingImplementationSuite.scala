package boon

import data.NonEmptySeq
import result.SuiteOutput
import result.SequentialPassData
import result.SequentialFailData
import result.SequentialNotRunData
import result.AssertionOutput
import result.Trace
import model.TestState
import model.AssertionData
import internal.instances._
import syntax.collection.positional

object MissingImplementationSuite extends SuiteLike("Missing Implementation Suite") {

  private val t1 = test("can handle missing implementations") {
      val so    = MissingImplFixtures.run
      val tests = so.tests

      sequentially(
        tests.length =?= 1 | "no of tests" and
        %@(tests.head) {
          _.fold(testRan, testThrew, testIgnored)
        }
      )
  }

  private def testRan(name: String, t1assertions: NonEmptySeq[AssertionOutput], state: TestState): AssertionData = {
    name =?= "test for missing implementations" | "test name"    and
    state =?= TestState.Failed                  | "test failed"  and
    positional(t1assertions, "assertions"){
      oneOrMore(
        asserNotImplementedTest("Boolean test", 14),
        asserNotImplementedTest("Int test", 15),
        asserNotImplementedTest("Unsafe test", 16)
      )
    }
  }

  private def asserNotImplementedTest(expectedName: String, expectedLoc: Int)(ao: AssertionOutput): AssertionData = {
    ao.fold(assertionFailed(expectedName, expectedLoc),
            assertionPassed,
            sequentialAssertionPassed,
            sequentialAssertionFailed)
  }

  private def testThrew(name: String, error: String, trac: Seq[Trace], loc: SourceLocation): AssertionData =
    fail(s"thrown test: $name") | "test type"

  private def testIgnored(name: String): AssertionData = fail(s"ignored test: $name") | "testType"

  private def assertionFailed(expectedName: String, expectedLoc: Int)(name: String, errors: NonEmptySeq[String],
    context: Map[String, String], loc: SourceLocation): AssertionData = {
    %@((), expectedName) { _ =>
     pass | "assertionOutput type" and
      name =?= expectedName | "assertion name" and
      errors =?= one("an implementation is missing") | "assertion error" and
      SuiteOutput.sourceLocation(loc).fold(
        fail("expected SourceLocation") | "error location"
      )(loc => loc.endsWith(s"ToBeImplementedSuite.scala:${expectedLoc}") || s"${expectedName}.error location" |> one(s"${expectedName}.loc" -> loc))
    }
  }

  private def assertionPassed(name: String): AssertionData = {
    fail(s"passed: $name") | "assertionOutput type"
  }

  private def sequentialAssertionPassed(name: String, passed: NonEmptySeq[SequentialPassData]): AssertionData = {
    fail(s"Sequential passed: $name") | "assertionOutput type"
  }

  private def sequentialAssertionFailed(name: String, failed: SequentialFailData,
    passed: Seq[SequentialPassData], notRun: Seq[SequentialNotRunData]): AssertionData = {
    fail(s"Sequential failed: $name") | "assertionOutput type"
  }

  override val tests = one(t1)
}

object MissingImplFixtures {

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(new ToBeImplementedSuite{}))
}

