package boon.sbt

import sbt.testing.Task
import sbt.testing.TaskDef
import sbt.testing.EventHandler
import sbt.testing.Logger
import sbt.testing.Status

import boon.Passable
import boon.Passed


import boon.Assertion
import boon.AssertionName
import boon.AssertionPassed
import boon.AssertionFailed
import boon.AssertionThrew
import boon.AssertionError
import boon.sbt.Event.createEvent
import boon.sbt.Event.createErrorEvent
import boon.sbt.Event.handleEvent
import boon.sbt.SuiteLoader.loadSuite

import scala.util.Failure
import scala.util.Success
import scala.util.Try


final class BoonTask2(val taskDef: TaskDef, cl: ClassLoader) extends Task {

  def tags(): Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {

    val startTime = System.currentTimeMillis()

    val tryRun =
      for {
        suiteClass   <- loadSuite(taskDef.fullyQualifiedName, cl)
        dSuiteResult <-  Try(boon.printers.IncrementalOutput.run(suiteClass.suite))
      } yield {
        log(dSuiteResult.suite.name.value, loggers)
        dSuiteResult.testResults.foreach { tr =>
          log("", loggers)
          val testName = tr.test.name.value
          log(testName, loggers)
          log("-" * testName.length,loggers)
          //log(tr.test.name.value, loggers)
          tr.assertionResults.foreach { dar =>

            val output = Try(dar.result.value()).fold(error => s"[e2] - ${error}", {
              case AssertionPassed(Assertion(AssertionName(name), _, _)) => s"[./]"
              case AssertionFailed(AssertionError(Assertion(AssertionName(name), _, _), error)) => s"[x] - (${error})"
              case AssertionThrew(AssertionName(name), error) => s"[e] - ${error.getMessage}"
            })
            log(s"${dar.assertion.name.value} ${output}", loggers)
            // log(output, loggers)
          }
        }
        ()
      }

      val endTime = System.currentTimeMillis()
      val timeTaken = endTime - startTime

       tryRun match {
        case Failure(error) =>
          handleEvent(createErrorEvent(taskDef, error, timeTaken), eventHandler)
          logError(s"failed to run suite: ${taskDef.fullyQualifiedName}", error, loggers)
        case Success(_) =>
          handleEvent(createEvent[Passable](taskDef, passableToStatus, Passed, timeTaken), eventHandler)
          log("done", loggers)
      }

    Array.empty[Task]
  }

  private def log(message: String, loggers: Array[Logger]): Unit = {
    loggers.foreach { log =>
      log.info(message)
    }
    // println(message)
  }

  private def logError(message: String, error: Throwable, loggers: Array[Logger]): Unit = {
    loggers.foreach{ l =>
      l.error(message)
      l.trace(error)
    }
  }

  private def passableToStatus(passable: boon.Passable): Status =
    passable match {
      case boon.Passed => Status.Success
      case boon.Failed => Status.Failure
    }
}