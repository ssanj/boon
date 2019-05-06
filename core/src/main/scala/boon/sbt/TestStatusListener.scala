package boon.sbt

import boon.model.stats.SuiteStats
import boon.model.SuiteResult
import boon.model.TestResult
import boon.model.SuiteState
import boon.model.TestState
import boon.Monoid

import boon.model.stats.AssertionCount
import boon.model.stats.StatusCount

import java.util.concurrent.atomic.AtomicReference

trait TestStatusListener {
  def suiteResult(result: SuiteResult): Unit
  def suiteFailed(reason: String): Unit
}

final class BoonTestStatusListener(atomicStats: AtomicReference[List[SuiteStats]]) extends TestStatusListener {

  override def suiteResult(result: SuiteResult): Unit = {
    val stats = Monoid[SuiteStats].mempty
    val suiteCounts =
      SuiteResult.suiteResultToSuiteState(result) match {
        case SuiteState.Passed =>  stats.copy(suites = stats.suites.copy(passed = stats.suites.passed + 1))
        case SuiteState.Failed => stats.copy(suites = stats.suites.copy(failed = stats.suites.failed + 1))
      }

      val testCounts =
        result.testResults.map(TestResult.testResultToTestState).foldLeft(suiteCounts) {
          case (acc, TestState.Passed)  => acc.copy(tests = acc.tests.copy(statusCount = acc.tests.statusCount.copy(passed  = acc.tests.statusCount.passed + 1)))
          case (acc, TestState.Failed)  => acc.copy(tests = acc.tests.copy(statusCount = acc.tests.statusCount.copy(failed  = acc.tests.statusCount.failed + 1)))
          case (acc, TestState.Ignored) => acc.copy(tests = acc.tests.copy(ignored = acc.tests.ignored + 1))
        }

      val newStats =
        result.testResults.map(TestResult.testResultToAssertionCount).foldLeft(testCounts) {
          case (acc, AssertionCount(StatusCount(pass, fail), notRun)) =>
            acc.copy(assertions =
                      acc.assertions.copy(statusCount =
                                            acc.assertions.statusCount.copy(passed = acc.assertions.statusCount.passed + pass,
                                                                            failed = acc.assertions.statusCount.failed + fail),
                                            notRun = acc.assertions.notRun + notRun))
        }

      val _ = atomicStats.updateAndGet(newStats +: _)
      ()
    }

  override def suiteFailed(reason: String): Unit = {
    val stats = Monoid[SuiteStats].mempty
    val newStats = stats.copy(suites = stats.suites.copy(failed =stats.suites.failed + 1))
    val _ = atomicStats.updateAndGet(newStats +: _)
    ()
  }
}