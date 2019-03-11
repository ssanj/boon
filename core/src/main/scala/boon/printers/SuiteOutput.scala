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

final case class CompositePassData(name: String)
final case class CompositeNotRunData(name: String)
final case class CompositeFailData(name: String, error: String, context: Map[String, String], location: Option[String])
sealed trait AssertionOutput extends Product with Serializable
final case class PassedOutput(name: String) extends AssertionOutput
final case class FailedOutput(name: String, error: String, context: Map[String, String], location: Option[String]) extends AssertionOutput
final case class CompositePassedOutput(name: String, passed: NonEmptySeq[CompositePassData]) extends AssertionOutput
//There should be only a single failure not a NES.
final case class CompositeFailedOutput(name: String, failed: CompositeFailData, passed: Seq[CompositePassData], notRun: Seq[CompositeNotRunData]) extends AssertionOutput


object AssertionOutput {

  final case class FoldSyntax(ao: AssertionOutput) {
    def fold[A](failed: (String, String, Map[String, String], Option[String]) => A,
                passed: String => A,
                compositePassed: (String, NonEmptySeq[CompositePassData]) => A,
                compositeFailed: (String, CompositeFailData, Seq[CompositePassData], Seq[CompositeNotRunData]) => A): A = ao match {
      case PassedOutput(name) => passed(name)
      case FailedOutput(name, error, context, loc) => failed(name, error, context, loc)
      case CompositePassedOutput(name, passed) => compositePassed(name, passed)
      case CompositeFailedOutput(name, failed, passed, notRun) => compositeFailed(name, failed, passed, notRun)
    }
  }

  implicit def foldAssertionOutput(ao: AssertionOutput): FoldSyntax = FoldSyntax(ao)
}

object SuiteOutput {

  def toSuiteOutput(suiteResult: SuiteResult): SuiteOutput = {
    val testOutputs = suiteResult.testResults.map { tr =>
      val assertionOutputs = tr.assertionResults.map {
        case AssertionPassed(SingleAssertion(AssertionName(name), _, _, _)) => PassedOutput(name)
        case AssertionPassed(CompositeAssertion(AssertionName(name), _, _, _)) => PassedOutput(name) //make this different
        case AssertionFailed(AssertionError(SingleAssertion(AssertionName(name), _, ctx, loc), error)) =>
          FailedOutput(name, error, ctx, sourceLocation(loc))
        case AssertionFailed(AssertionError(CompositeAssertion(AssertionName(name), _, ctx, loc), error)) =>
          FailedOutput(name, error, ctx, sourceLocation(loc))

        case AssertionThrew(AssertionThrow(AssertionName(name), error, loc)) =>
          FailedOutput(name, error.getMessage, Map.empty[String, String], sourceLocation(loc))

        case CompositeAssertionAllPassed(AssertionName(name), passed) => CompositePassedOutput(name, passed.map(an => CompositePassData(an.name.value)))
        case CompositeAssertionFirstFailed(FirstFailed(AssertionName(name), failed,  passed, notRun)) =>
            val failedData =
              failed.fold[CompositeFailData]({
                case CompositeFail(AssertionError(SingleAssertion(AssertionName(name1), _, ctx, loc), error)) =>
                  CompositeFailData(name1, error, ctx, sourceLocation(loc))
                case CompositeFail(AssertionError(CompositeAssertion(AssertionName(name1), _, ctx, loc), error)) =>
                  CompositeFailData(name1, error, ctx, sourceLocation(loc))
                }, ct => CompositeFailData(ct.value.name.value, ct.value.value.getMessage, Map.empty[String, String], sourceLocation(ct.value.location))
              )
            CompositeFailedOutput(name, failedData, passed.map(an => CompositePassData(an.name.value)), notRun.map(an => CompositeNotRunData(an.name.value)))
      }

      TestOutput(tr.test.name.value, assertionOutputs, testResultToPassable(tr))
    }

    SuiteOutput(suiteResult.suite.name.value, testOutputs, suiteResultToPassable(suiteResult))
  }

  def assertionName(ao: AssertionOutput): String = ao match {
    case PassedOutput(name)                   => name
    case FailedOutput(name, _, _, _)          => name
    case CompositePassedOutput(name, _)       => name
    case CompositeFailedOutput(name, _, _, _) => name
  }

  def sourceLocation(loc: SourceLocation): Option[String] =
    loc.filePath.map(filePath => s"${filePath}:${loc.line}")
}