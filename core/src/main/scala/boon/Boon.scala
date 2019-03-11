package boon

import scala.util.Try

object Boon {

  def testable[A](a1: Defer[A], a2: Defer[A], et: EqualityType)(implicit E: Equality[A], D: Difference[A]): Defer[Testable] = {
    val t = () => new Testable {
      type Actual = A
      val value1: Defer[Actual] = a1
      val value2: Defer[Actual] = a2
      val equality = E
      val difference = D
      val equalityType = et
    }

    Defer[Testable](t)
  }

  def defineAssertion[A](name: => String, gen: (Defer[A], Defer[A]), equalityType: EqualityType)(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): Assertion =
    defineAssertionWithContext[A](name, gen, equalityType, Map.empty[String, String])

  def defineAssertionWithContext[A](name: => String, gen: (Defer[A], Defer[A]), equalityType: EqualityType, context: Map[String, String])(implicit E: Equality[A], D: Difference[A], loc: SourceLocation): Assertion =
    SingleAssertion(AssertionName(name), {
      val (a1, a2) = gen
      testable[A](a1, a2, equalityType)
    }, context, loc)

  def defineCompositeAssertion(name: => String, assertions: NonEmptySeq[Assertion], context: Map[String, String], loc: SourceLocation): Assertion =
    CompositeAssertion(AssertionName(name), assertions, context, loc)

  private case class ResultCollector(pass: Vector[AssertionResult], fail: Option[AssertionFailure], notRun: Vector[Assertion])

  def runAssertion(assertion: Assertion): AssertionResult = {
      assertion match {
        case assertion: SingleAssertion =>
          Try {
            val testable = assertion.testable.run()
            val value1 = testable.value1.run()
            val value2 = testable.value2.run()

            val eqFunc = testable.equalityType.fold(testable.equality.neql _, testable.equality.eql _)

            if (eqFunc(value1, value2)) AssertionPassed(assertion)
            else AssertionFailed(AssertionError(assertion, testable.difference.diff(value1, value2)))

          }.fold(t => AssertionThrew(AssertionThrow(assertion.name, t, assertion.location)), identity _)

        case assertion: CompositeAssertion =>
          val init = ResultCollector(pass = Vector.empty[AssertionResult], fail = None, notRun = Vector.empty[Assertion])

          val results = assertion.assertions.foldLeft(init){ (acc, a1) =>
            acc.fail.fold({
              val a1Result = runAssertion(a1)
              a1Result match {
                case (_: AssertionPassed | _: CompositeAssertionAllPassed) => acc.copy(pass = acc.pass :+ a1Result)
                case af: AssertionFailed => acc.copy(fail = Some(SingleAssertionFailed(af.value)))
                case at: AssertionThrew  => acc.copy(fail = Some(SingleAssertionThrew(at.value)))
                case caff: CompositeAssertionFirstFailed => acc.copy(fail = Some(CompositeAssertionFailed(caff.value)))
              }
            })(_ => acc.copy(notRun = acc.notRun :+ a1))
          }

          results.fail.fold[AssertionResult]({
            val passed = results.pass.map(ar => CompositePass(assertionNameFromResult(ar)))
            CompositeAssertionAllPassed(assertion.name, NonEmptySeq.nes(passed.head, passed.tail:_*))
          })({ failure =>
              val failed = failure match {
                case saf : SingleAssertionFailed => Left[CompositeFail, CompositeThrew](CompositeFail(saf.value))
                case sat : SingleAssertionThrew  => Right[CompositeFail, CompositeThrew](CompositeThrew(sat.value))
                case caf: CompositeAssertionFailed => caf.value.failed
              }

              val passed = results.pass.map(ar => CompositePass(assertionNameFromResult(ar)))
              val notRun = results.notRun.map(assertion => CompositeNotRun(Assertion.assertionName(assertion)))
              CompositeAssertionFirstFailed(FirstFailed(assertion.name, failed, passed, notRun))
          })
      }
  }

  def runTest(dTest: DeferredTest): TestResult = {
    val assertionResults = dTest.assertions.map(runAssertion)
    TestResult(dTest, assertionResults)
  }

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(suiteLike.suite)

  def runSuite(dSuite: DeferredSuite): SuiteResult = {
    val testResults = dSuite.tests.map(runTest)
    SuiteResult(dSuite, testResults)
  }

  def assertionResultToPassable(ar: AssertionResult): Passable = ar match {
    case _: AssertionPassed => Passed
    case _: AssertionFailed => Failed
    case _: AssertionThrew  => Failed
    case _: CompositeAssertionAllPassed => Passed
    case _: CompositeAssertionFirstFailed => Failed

  }

  def testResultToPassable(tr: TestResult): Passable = {
    val failedOp = tr.assertionResults.map(assertionResultToPassable).find {
      case Failed => true
      case Passed => false
    }

    failedOp.fold[Passable](Passed)(_ => Failed)
  }

  def suiteResultToPassable(sr: SuiteResult): Passable = {
    val failedOp = sr.testResults.map(testResultToPassable).find {
      case Failed => true
      case Passed => false
    }

   failedOp.fold[Passable](Passed)(_ => Failed)
  }

  def assertionNameFromResult(ar: AssertionResult): AssertionName = ar match {
    case AssertionPassed(assertion) => Assertion.assertionName(assertion)
    case AssertionFailed(AssertionError(assertion, _)) => Assertion.assertionName(assertion)
    case AssertionThrew(AssertionThrow(name, _, _)) => name
    case CompositeAssertionAllPassed(name, _) => name
    case CompositeAssertionFirstFailed(FirstFailed(name, _, _, _)) => name
  }
}

