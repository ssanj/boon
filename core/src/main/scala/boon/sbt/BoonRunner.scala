package boon.sbt

import sbt.testing.Runner
import sbt.testing.Task
import sbt.testing.TaskDef

import boon.printers.PrinterSetting
import boon.printers.SimplePrinter
import boon.model.stats.SuiteStats
import boon.Monoid

import java.util.concurrent.atomic.AtomicReference



final class BoonRunner(
  val args: Array[String],
  val remoteArgs: Array[String],
  classLoader: ClassLoader)
  extends Runner {

  private val statsVecAtomic = new AtomicReference[List[SuiteStats]](List.empty[SuiteStats])

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

    val stats = statsVecAtomic.get

    if (stats.nonEmpty) {
      val stats         = statsVecAtomic.get.foldLeft(Monoid[SuiteStats].mempty)(Monoid[SuiteStats].mappend)
      val suiteLine     = s"Suites: passed - ${stats.suites.passed}, failed - ${stats.suites.failed}"
      val testLine      = s"Test: passed - ${stats.tests.statusCount.passed}, failed - ${stats.tests.statusCount.failed}, ignored: ${stats.tests.ignored}"
      val assertionLine = s"Assertions: passed - ${stats.assertions.statusCount.passed}, failed - ${stats.assertions.statusCount.failed}, notRun: ${stats.assertions.notRun}"

      val underscoreLength =
        max(max(max(suiteLine.length, testLine.length), assertionLine.length), 1.0D).toInt

      s"${"-" * underscoreLength}\n" +
        s"${suiteLine}\n" +
        s"${testLine}\n"  +
        s"${assertionLine}\n" +
      s"${"-" * underscoreLength}\n"
    } else {
      val message = "No tests run through Boon"
      val underscoreLength = message.length

      s"${"-" * underscoreLength}\n" +
        s"${message}\n" +
      s"${"-" * underscoreLength}"
    }
  }
}
