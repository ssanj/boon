package boon
package model

import boon.data.NonEmptySeq
import syntax.collection._

object internal {

  final case class SingleTestPassed(testName: TestName, triple: AssertionTriple)
  final case class SingleTestFailed(testName: TestName, assertionName: AssertionName, context: Map[String, String], loc: SourceLocation, errors: NonEmptySeq[String])

  def getSingleTestPassed(tr: TestResult): Option[SingleTestPassed] = tr match {
      case SingleTestResult(DeferredTest(testName, _, _),
        NonEmptySeq(SingleAssertionResult(AssertionResultPassed(triple)), _)) =>
          Some(SingleTestPassed(testName, triple))
      case _ => None
  }

  def getSingleTestFailed(tr: TestResult): Option[SingleTestFailed] = tr match {
    case SingleTestResult(DeferredTest(testName, _, _),
      NonEmptySeq(SingleAssertionResult(AssertionResultFailed(assertionError)), _)) =>
        Some(SingleTestFailed(testName,
                              assertionError.assertion.name,
                              assertionError.assertion.context,
                              assertionError.assertion.location,
                              assertionError.errors))
    case _ => None
  }

  def singleTestPassed(
      testName: String,
      assertionName: String,
      context: Map[String, String],
      location: Int)(result: TestResult): AssertionData =  {
        getSingleTestPassed(result) match {
          case Some(SingleTestPassed(TestName(tName), AssertionTriple(AssertionName(aName), ctx, loc))) =>
            tName =?= testName | "test name"           and
            aName =?= assertionName | "assertion name" and
            ctx =?= context | "context"                and
            loc.line =?= location | "location"

          case None => invalid(s"Expected test success, got: $result") | "test type"
        }

  }

  def singleTestFailed(
      testName: String,
      assertionName: String,
      context: Map[String, String],
      location: Int,
      error: String)(result: TestResult): AssertionData =  {
        getSingleTestFailed(result) match {
          case Some(SingleTestFailed(TestName(tName), AssertionName(aName), ctx, loc, errors)) =>
            tName =?= testName | "test name"           and
            aName =?= assertionName | "assertion name" and
            ctx =?= context | "context"                and
            loc.line =?= location || "location"  |> (one("line" -> loc.toString))       and
            positional(errors, "assertion.errors")(one(_ =?= error | "error message"))

          case None => invalid(s"Expected test failure, got: $result") | "test type"
        }
  }
}

