package boon
package result

//  SuiteName [passed]
//  - test1 [passed]
//    - assertion1 [/]
//    - assertion2 [/]
// - test2 [failed]
//    - assertion1 [x]
//      => [] != ["studio"]
//    - assertion2 [/]
//

import boon.model._
import boon.model.TestResult.testResultToPassable
import boon.model.SuiteResult.suiteResultToPassable
import boon.result.Exception.getTraces


final case class SuiteOutput(name: String, tests: NonEmptySeq[TestOutput], pass: Passable)
final case class TestOutput(name: String, assertions: NonEmptySeq[AssertionOutput], pass: Passable)

final case class SequentialPassData(name: String)
final case class SequentialNotRunData(name: String)
final case class SequentialFailData(name: String, error: String, context: Map[String, String], location: Option[String])

sealed trait AssertionOutput extends Product with Serializable
final case class PassedOutput(name: String) extends AssertionOutput
final case class FailedOutput(name: String, error: String, trace: Seq[Trace], context: Map[String, String], location: Option[String]) extends AssertionOutput
final case class SequentialPassedOutput(name: String, passed: NonEmptySeq[SequentialPassData]) extends AssertionOutput
final case class SequentialFailedOutput(name: String, failed: SequentialFailData, passed: Seq[SequentialPassData], notRun: Seq[SequentialNotRunData]) extends AssertionOutput


object AssertionOutput {

  final case class FoldSyntax(ao: AssertionOutput) {
    def fold[A](failed: (String, String, Map[String, String], Option[String]) => A,
                passed: String => A,
                sequentialPassed: (String, NonEmptySeq[SequentialPassData]) => A,
                sequentialFailed: (String, SequentialFailData, Seq[SequentialPassData], Seq[SequentialNotRunData]) => A): A = ao match {
      case PassedOutput(name) => passed(name)
      case FailedOutput(name, error, trace, context, loc) => failed(name, error, context, loc)
      case SequentialPassedOutput(name, passed) => sequentialPassed(name, passed)
      case SequentialFailedOutput(name, failed, passed, notRun) => sequentialFailed(name, failed, passed, notRun)
    }
  }

  implicit def foldAssertionOutput(ao: AssertionOutput): FoldSyntax = FoldSyntax(ao)
}

object SuiteOutput {

  val stackDepth = 5

  def toSuiteOutput(suiteResult: SuiteResult): SuiteOutput = {
    val testOutputs = suiteResult.testResults.map { tr =>
      val assertionOutputs: NonEmptySeq[AssertionOutput] = tr match {
        case SingleTestResult(test, assertionResults) =>
          assertionResults.map {
            case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(name), _, _))) => PassedOutput(name)
            case SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(name), _, ctx, loc), error))) =>
              FailedOutput(name, error, Nil, ctx, sourceLocation(loc))

            case SingleAssertionResult(AssertionResultThrew(AssertionThrow(AssertionName(name), error, loc))) =>
              FailedOutput(name, error.getMessage, getTraces(error, stackDepth), Map.empty[String, String], sourceLocation(loc))
          }

        case CompositeTestResult(StoppedOnFirstFailed(_, FirstFailed(AssertionName(name), failed,  passed, notRun))) =>
            val failedData =
              failed.fold[SequentialFailData]({
                case SequentialFail(AssertionError(Assertion(AssertionName(name1), _, ctx, loc), error)) =>
                  SequentialFailData(name1, error, ctx, sourceLocation(loc))
                }, ct => SequentialFailData(ct.value.name.value, ct.value.value.getMessage, Map.empty[String, String], sourceLocation(ct.value.location))
              )
            NonEmptySeq.one(SequentialFailedOutput(name, failedData, passed.map(an => SequentialPassData(an.name.value)), notRun.map(an => SequentialNotRunData(an.name.value))))

        case CompositeTestResult(AllPassed(TestName(name), passed)) =>
          NonEmptySeq.one(SequentialPassedOutput(name, passed.map(an => SequentialPassData(an.name.value))))

        case TestThrewResult(ThrownTest(TestName(name), error, loc)) =>
          NonEmptySeq.one(FailedOutput(name, error.getMessage, getTraces(error, stackDepth), Map.empty[String, String], sourceLocation(loc)))
      }

      TestOutput(TestResult.testName(tr).value, assertionOutputs, testResultToPassable(tr))
    }

    SuiteOutput(suiteResult.suite.name.value, testOutputs, suiteResultToPassable(suiteResult))
  }

  def assertionName(ao: AssertionOutput): String = ao match {
    case PassedOutput(name)                   => name
    case FailedOutput(name, _, _, _, _)       => name
    case SequentialPassedOutput(name, _)       => name
    case SequentialFailedOutput(name, _, _, _) => name
  }

  def sourceLocation(loc: SourceLocation): Option[String] =
    loc.filePath.map(filePath => s"${filePath}:${loc.line}")
}