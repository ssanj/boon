package boon.sbt

import sbt.testing.Runner
import sbt.testing.Task
import sbt.testing.TaskDef

import boon.printers.PrinterSetting
import boon.printers.SimplePrinter
import boon.model.stats.SuiteStats
import boon.model.stats.StatusCount
import boon.model.stats.AssertionCount

import java.util.concurrent.atomic.AtomicReference



final class BoonRunner(
  val args: Array[String],
  val remoteArgs: Array[String],
  classLoader: ClassLoader)
  extends Runner {

  private val statsVecAtomic = new AtomicReference[Vector[SuiteStats]](Vector.empty[SuiteStats])

  //use default printer for now, change to use from args
  override def tasks(list: Array[TaskDef]): Array[Task] = {
    list.map(new BoonTask(
                  _,
                  classLoader,
                  (so, c, print) =>
                    SimplePrinter(so, PrinterSetting.defaults(c), print),
                  new BoonTestStatusListener(statsVecAtomic)
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
