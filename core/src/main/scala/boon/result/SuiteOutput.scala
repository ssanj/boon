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

final case class SuiteOutput(name: String, tests: NonEmptySeq[TestOutput], state: SuiteState)

sealed trait TestOutput extends Product with Serializable
final case class TestPassedOutput(name: String, assertions: NonEmptySeq[AssertionOutput], state: TestState) extends TestOutput
final case class TestThrewOutput(name: String, error: String, trace: Seq[Trace], loc: SourceLocation) extends TestOutput
final case class TestIgnoredOutput(name: String) extends TestOutput

final case class SequentialPassData(name: String)
final case class SequentialNotRunData(name: String)
final case class SequentialFailData(name: String, error: String, context: Map[String, String], location: Option[String])

sealed trait AssertionOutput extends Product with Serializable
final case class PassedOutput(name: String) extends AssertionOutput

final case class FailedOutput(name: String, error: String, trace: Seq[Trace], context: Map[String, String], location: SourceLocation) extends AssertionOutput
final case class SequentialPassedOutput(name: String, passed: NonEmptySeq[SequentialPassData]) extends AssertionOutput
final case class SequentialFailedOutput(name: String, failed: SequentialFailData, passed: Seq[SequentialPassData], notRun: Seq[SequentialNotRunData]) extends AssertionOutput


object AssertionOutput {

  final case class FoldSyntax(ao: AssertionOutput) {
    def fold[A](failed: (String, String, Map[String, String], SourceLocation) => A,
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

object TestOutput {

  final case class FoldSyntax(to: TestOutput) {
    def fold[A](passed: (String, NonEmptySeq[AssertionOutput], TestState) => A,
                threw: (String, String, Seq[Trace], SourceLocation) => A,
                ignored: String => A): A = to match {
      case TestPassedOutput(name, assertions, state) => passed(name, assertions, state)
      case TestThrewOutput(name, error, trace, loc)  => threw(name, error, trace, loc)
      case TestIgnoredOutput(name)                   => ignored(name)
    }
  }

  implicit def foldTestOutput(to: TestOutput): FoldSyntax = FoldSyntax(to)
}

object SuiteOutput {

  val stackDepth = 5

  def toSuiteOutput(suiteResult: SuiteResult): SuiteOutput = {
    val testOutputs = suiteResult.testResults.map { tr =>
      tr match {
        case SingleTestResult(test, assertionResults) =>

          val assertionOutputs: NonEmptySeq[AssertionOutput] =
            assertionResults.map {
              case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(name), _, _))) => PassedOutput(name)
              case SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(name), _, ctx, loc), error))) =>
                FailedOutput(name, error, Nil, ctx, loc)

              case SingleAssertionResult(AssertionResultThrew(AssertionThrow(AssertionName(name), error, loc))) =>
                FailedOutput(name, error.getMessage, getTraces(error, stackDepth), Map.empty[String, String], loc)
            }

          TestPassedOutput(TestResult.testName(tr).value, assertionOutputs, testResultToPassable(tr))

        case CompositeTestResult(StoppedOnFirstFailed(_, FirstFailed(AssertionName(name), failed,  passed, notRun))) =>
            val failedData =
              failed.fold[SequentialFailData]({
                case SequentialFail(AssertionError(Assertion(AssertionName(name1), _, ctx, loc), error)) =>
                  SequentialFailData(name1, error, ctx, sourceLocation(loc))
                }, ct => SequentialFailData(ct.value.name.value, ct.value.value.getMessage, Map.empty[String, String], sourceLocation(ct.value.location))
              )

            val assertionOutputs: NonEmptySeq[AssertionOutput] =
              one(SequentialFailedOutput(name, failedData, passed.map(an => SequentialPassData(an.name.value)), notRun.map(an => SequentialNotRunData(an.name.value))))

          TestPassedOutput(TestResult.testName(tr).value, assertionOutputs, testResultToPassable(tr))

        case CompositeTestResult(AllPassed(TestName(name), passed)) =>
          val assertionOutputs: NonEmptySeq[AssertionOutput] =
            one(SequentialPassedOutput(name, passed.map(an => SequentialPassData(an.name.value))))

          TestPassedOutput(TestResult.testName(tr).value, assertionOutputs, testResultToPassable(tr))

        case TestThrewResult(ThrownTest(TestName(name), error, loc)) =>
          TestThrewOutput(name, error.getMessage, getTraces(error, stackDepth), loc)

        case TestIgnoredResult(TestName(name)) =>
          TestIgnoredOutput(name)
      }
    }

    SuiteOutput(suiteResult.suite.name.value, testOutputs, suiteResultToPassable(suiteResult))
  }

  def assertionName(ao: AssertionOutput): String = ao match {
    case PassedOutput(name)                    => name
    case FailedOutput(name, _, _, _, _)        => name
    case SequentialPassedOutput(name, _)       => name
    case SequentialFailedOutput(name, _, _, _) => name
  }

  def sourceLocation(loc: SourceLocation): Option[String] =
    loc.filePath.map(filePath => s"${filePath}:${loc.line}")
}