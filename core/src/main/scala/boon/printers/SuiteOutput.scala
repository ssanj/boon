package boon
package printers

//  SuiteName [passed]
//  - test1 [passed]
//    - assertion1 [/]
//    - assertion2 [/]
// - test2 [failed]
//    - assertion1 [x]
//      => [] != ["studio"]
//    - assertion2 [/]
//

import Boon.suiteResultToPassable
import Boon.testResultToPassable

final case class SuiteOutput(name: String, tests: NonEmptySeq[TestOutput], pass: Passable)
final case class TestOutput(name: String, assertions: NonEmptySeq[AssertionOutput], pass: Passable)

sealed trait AssertionOutput extends Product with Serializable
final case class PassedOutput(name: String) extends AssertionOutput
final case class FailedOutput(name: String, error: String, context: Map[String, String], location: Option[String]) extends AssertionOutput

object AssertionOutput {

  final case class FoldSyntax(ao: AssertionOutput) {
    def fold[A](failed: (String, String, Map[String, String], Option[String]) => A, passed: String => A): A = ao match {
      case PassedOutput(name) => passed(name)
      case FailedOutput(name, error, context, loc) => failed(name, error, context, loc)
    }
  }

  implicit def foldAssertionOutput(ao: AssertionOutput): FoldSyntax = FoldSyntax(ao)
}

object SuiteOutput {

  def toSuiteOutput(suiteResult: SuiteResult): SuiteOutput = {
    val testOutputs = suiteResult.testResults.map { tr =>
      val assertionOutputs = tr.assertionResults.map {
        case AssertionPassed(Assertion(AssertionName(name), _, _, _)) => PassedOutput(name)
        case AssertionFailed(AssertionError(Assertion(AssertionName(name), _, ctx, loc), error)) =>
          FailedOutput(name, error, ctx, sourceLocation(loc))
        case AssertionThrew(AssertionName(name), error) =>
          FailedOutput(name, error.getMessage, Map.empty[String, String], None)
      }

      TestOutput(tr.test.name.value, assertionOutputs, testResultToPassable(tr))
    }

    SuiteOutput(suiteResult.suite.name.value, testOutputs, suiteResultToPassable(suiteResult))
  }

  def assertionName(ao: AssertionOutput): String = ao match {
    case PassedOutput(name) => name
    case FailedOutput(name, _, _, _) => name
  }

  def sourceLocation(loc: SourceLocation): Option[String] =
    loc.filePath.map(filePath => s"${filePath}:${loc.line}")
}