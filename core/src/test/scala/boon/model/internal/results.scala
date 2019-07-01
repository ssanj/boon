package boon
package model

import boon.data.NonEmptySeq

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
}

