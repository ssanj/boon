package boon.sbt

import boon.model.stats.StatusCount
import boon.model.stats.SuiteStats
import boon.model.stats.AssertionCount
import boon.model.SuiteResult
import boon.model.TestResult
import boon.model.Failed
import boon.model.Passed

import java.util.concurrent.atomic.AtomicReference

trait TestStatusListener {
  def suiteResult(result: SuiteResult): Unit
  def suiteFailed(reason: String): Unit
}


final class BoonTestStatusListener(atomicStats: AtomicReference[Vector[SuiteStats]]) extends TestStatusListener {

  override def suiteResult(result: SuiteResult): Unit = {
    val stats = SuiteStats(suites = StatusCount(0, 0), tests = StatusCount(0, 0), assertions = AssertionCount(StatusCount(0, 0), 0))
    val suiteCounts =
      SuiteResult.suiteResultToPassable(result) match {
        case Passed =>  stats.copy(suites = stats.suites.copy(passed = stats.suites.passed + 1))
        case Failed => stats.copy(suites = stats.suites.copy(failed = stats.suites.failed + 1))
      }

      val testCounts =
        result.testResults.map(TestResult.testResultToPassable).foldLeft(suiteCounts) {
          case (acc, Passed) => acc.copy(tests = acc.tests.copy(passed = acc.tests.passed + 1))
          case (acc, Failed) => acc.copy(tests = acc.tests.copy(failed = acc.tests.failed + 1))
        }

      val newStats =
        result.testResults.map(TestResult.testResultToAssertionCount).foldLeft(testCounts) {
          case (acc, (pass, fail, notRun)) =>
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
    val stats = SuiteStats(suites = StatusCount(0, 0), tests = StatusCount(0, 0), assertions = AssertionCount(StatusCount(0, 0), 0))
    val newStats = stats.copy(suites = stats.suites.copy(failed =stats.suites.failed + 1))
    val _ = atomicStats.updateAndGet(newStats +: _)
    ()
  }
}