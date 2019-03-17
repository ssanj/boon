package boon.sbt

import sbt.testing.Runner
import sbt.testing.Task
import sbt.testing.TaskDef

import boon.printers.PrinterSetting
import boon.printers.SimplePrinter
import boon.model.SuiteResult
import boon.model.TestResult
import boon.model.Failed
import boon.model.Passed

import java.util.concurrent.atomic.AtomicReference

trait TestStatusListener {
  def suiteResult(result: SuiteResult): Unit
  def suiteFailed(reason: String): Unit
}

final case class StatusCount(passed: Int, failed: Int)

final case class AssertionCount(statusCount: StatusCount, notRun: Int)

//This could be a monoid
final case class SuiteStats(suites: StatusCount, tests: StatusCount, assertions: AssertionCount)


final class BoonRunner(
  val args: Array[String],
  val remoteArgs: Array[String],
  classLoader: ClassLoader)
  extends Runner {

  private val statsVecAtomic = new AtomicReference[Vector[SuiteStats]](Vector.empty[SuiteStats])

  //Should this run Async?
  private val statusListener = new TestStatusListener {

    def suiteResult(result: SuiteResult): Unit = {
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

        val _ = statsVecAtomic.updateAndGet(newStats +: _)
        ()
      }

    def suiteFailed(reason: String): Unit = {
      val stats = SuiteStats(suites = StatusCount(0, 0), tests = StatusCount(0, 0), assertions = AssertionCount(StatusCount(0, 0), 0))
      val newStats = stats.copy(suites = stats.suites.copy(failed =stats.suites.failed + 1))
      val _ = statsVecAtomic.updateAndGet(newStats +: _)
      ()
    }
  }

  //use default printer for now, change to use from args
  override def tasks(list: Array[TaskDef]): Array[Task] = {
    list.map(new BoonTask(
                  _,
                  classLoader,
                  (so, c, print) =>
                    SimplePrinter(so, PrinterSetting.defaults(c), print),
                  statusListener
             )
    )
  }

  override def done(): String = {

    import math.max

    val initial = SuiteStats(suites = StatusCount(0, 0), tests = StatusCount(0, 0), assertions = AssertionCount(StatusCount(0, 0), 0))

    val stats = statsVecAtomic.get.reverse.foldLeft(initial) {
      case (SuiteStats(suites1, tests1, assertions1), SuiteStats(suites2, tests2, assertions2)) =>
        SuiteStats(suites     = StatusCount(suites1.passed + suites2.passed, suites1.failed + suites2.failed),
                   tests      = StatusCount(tests1.passed + tests2.passed, tests1.failed + tests2.failed),
                   assertions = AssertionCount(
                                  StatusCount(assertions1.statusCount.passed + assertions2.statusCount.passed,
                                              assertions1.statusCount.failed + assertions2.statusCount.failed
                                  ), assertions1.notRun + assertions2.notRun)
        )
    }

    val suiteLine     = s"Suites: passed - ${stats.suites.passed}, failed - ${stats.suites.failed}"
    val testLine      = s"Test: passed - ${stats.tests.passed}, failed - ${stats.tests.failed}"
    val assertionLine = s"Assertions: passed - ${stats.assertions.statusCount.passed}, failed - ${stats.assertions.statusCount.failed}, notRun: ${stats.assertions.notRun}"

    val underscoreLength =
      max(max(max(suiteLine.length, testLine.length), assertionLine.length), 1.0D).toInt

    s"${"-" * underscoreLength}\n" +
      s"${suiteLine}\n" +
      s"${testLine}\n"  +
      s"${assertionLine}\n" +
    s"${"-" * underscoreLength}\n"
  }
}
