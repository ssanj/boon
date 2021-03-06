package boon.sbt

import sbt.testing.Runner
import sbt.testing.Task
import sbt.testing.Logger
import sbt.testing.TaskDef

import boon.printers.SimplePrinter
import boon.printers.BoonPrinter
import boon.printers.ColourOutput
import boon.model.stats.SuiteStats
import boon.model.SuiteResult
import boon.result.SuiteOutput
import boon.Monoid

import java.util.concurrent.atomic.AtomicReference

sealed trait TaskResult
case class SuiteResultTask(result: SuiteResult, loggers: Array[Logger]) extends TaskResult
case class SuiteFailureTask(message: String, error: Throwable, loggers: Array[Logger]) extends TaskResult


final class BoonRunner(
  val args: Array[String],
  val remoteArgs: Array[String],
  classLoader: ClassLoader)
  extends Runner {

  private val statsVecAtomic = new AtomicReference[List[SuiteStats]](List.empty[SuiteStats])

  private val taskResultsVecAtomic = new AtomicReference[List[TaskResult]](List.empty[TaskResult])

  private def createDefaultPrinter: BoonPrinter = SimplePrinter

  private def createCustomPrinter(className: String): BoonPrinter = {
    Loaders.loadPrinter(className, classLoader).fold(error => {
      Console.err.println(s"could not load custom BoonPrinter: ${className} due to ${error}")
      Console.err.println(s"using default printer instance")
      createDefaultPrinter
    }, identity _)
  }

  private def createPrinter(options: Array[String]): BoonPrinter = {
    options match {
      case Array("-P", printerClass) => createCustomPrinter(printerClass)
      case _ => createDefaultPrinter
    }
  }

  //use default printer for now, change to use from args
  override def tasks(list: Array[TaskDef]): Array[Task] = {
      list.map(new BoonTask(_, classLoader, new BoonTestStatusListener(statsVecAtomic, taskResultsVecAtomic)))
  }

  private def logResults(): Unit = {
    val printer = createPrinter(args)
    val results = taskResultsVecAtomic.get
    results.foreach {
      case SuiteResultTask(result, loggers)          => logValidSuite(loggers, printer, SuiteOutput.toSuiteOutput(result))
      case SuiteFailureTask(message, error, loggers) => logSuiteError(loggers, message, error)
    }
  }


  private def logValidSuite(loggers: Array[Logger], printer: BoonPrinter, suiteOutput: SuiteOutput): Unit = {
    loggers.foreach { logger =>
      printer.print(ColourOutput.fromBoolean(logger.ansiCodesSupported()), logger.info(_), suiteOutput)
    }

  }

  private def logSuiteError(loggers: Array[Logger], message: String, error: Throwable): Unit = {
    loggers.foreach { logger =>
     logger.error(message)
     logger.trace(error)
    }
  }

  override def done(): String = {

    logResults()

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
