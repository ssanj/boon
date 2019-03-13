package boon

import boon.model._

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

  private case class ResultCollector(pass: Vector[AssertionResult], fail: Option[AssertionFailure], notRun: Vector[Assertion])

  def runAssertion(assertion: Assertion): AssertionResult = {
      assertion match {
        case assertion: SingleAssertion =>
          Try {
            val testable = assertion.testable.run()
            val value1 = testable.value1.run()
            val value2 = testable.value2.run()

            val eqFunc = testable.equalityType.fold(testable.equality.neql _, testable.equality.eql _)

            if (eqFunc(value1, value2)) SingleAssertionResult(AssertionPassed(AssertionTriple(assertion.name, assertion.context, assertion.location)))
            else SingleAssertionResult(AssertionFailed(AssertionError(assertion, testable.difference.diff(value1, value2))))

          }.fold(t => SingleAssertionResult(AssertionThrew(AssertionThrow(assertion.name, t, assertion.location))), identity _)
      }
  }

  def runTest(dTest: DeferredTest): TestResult = {
      dTest.combinator match {
        case Independent => SingleTestResult(dTest, dTest.assertions.map(runAssertion))
        case Sequential =>
          val zero = ResultCollector(pass = Vector.empty[AssertionResult], fail = None, notRun = Vector.empty[Assertion])

          val results = dTest.assertions.foldLeft(zero){ (acc, a1) =>
            acc.fail.fold({
              val a1Result = runAssertion(a1)
              a1Result match { //do we need to create separate types given that we have separate Single/Composite types?
                case SingleAssertionResult(_: AssertionPassed)  => acc.copy(pass = acc.pass :+ a1Result)
                case SingleAssertionResult(af: AssertionFailed) => acc.copy(fail = Some(SingleAssertionFailed(af.value)))
                case SingleAssertionResult(at: AssertionThrew)  => acc.copy(fail = Some(SingleAssertionThrew(at.value)))
              }
            })(_ => acc.copy(notRun = acc.notRun :+ a1))
          }

          results.fail.fold[TestResult]({
            val passed = results.pass.map(ar => SequentialPass(AssertionResult.assertionNameFromResult(ar)))
            CompositeTestResult(AllPassed(dTest.name, NonEmptySeq.nes(passed.head, passed.tail:_*)))
          })({ failure =>
              val failed = failure match {
                case saf : SingleAssertionFailed => Left[SequentialFail, SequentialThrew](SequentialFail(saf.value))
                case sat : SingleAssertionThrew  => Right[SequentialFail, SequentialThrew](SequentialThrew(sat.value))
                // case caf: CompositeAssertionFailed => caf.value.failed
              }

              val failedAssertionName = failed.fold(cf => Assertion.assertionName(cf.value.assertion), _.value.name)

              val passed = results.pass.map(ar => SequentialPass(AssertionResult.assertionNameFromResult(ar)))
              val notRun = results.notRun.map(assertion => SequentialNotRun(Assertion.assertionName(assertion)))
              CompositeTestResult(StoppedOnFirstFailed(dTest.name, FirstFailed(failedAssertionName, failed, passed, notRun)))
          })
      }
  }

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(suiteLike.suite)

  def runSuite(dSuite: DeferredSuite): SuiteResult = {
    val testResults = dSuite.tests.map(runTest)
    SuiteResult(dSuite, testResults)
  }
}

