package boon.sbt

import sbt.testing.Task
import sbt.testing.TaskDef
import sbt.testing.EventHandler
import sbt.testing.Logger
import sbt.testing.Status

import boon.Boon
import boon.model.SuiteState
import boon.model.SuiteResult

import boon.sbt.Event.createEvent
import boon.sbt.Event.createErrorEvent
import boon.sbt.Event.handleEvent
import boon.sbt.Loaders.loadSuite

import scala.util.Failure
import scala.util.Success
import scala.util.Try

final class BoonTask(val taskDef: TaskDef,
                          cl: ClassLoader,
                          statusLister: TestStatusListener) extends Task {

  def tags(): Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
      val suiteTry                    = loadSuite(taskDef.fullyQualifiedName, cl)
      val (timeTaken, suiteResultTry) = runTimed { () =>
        suiteTry.flatMap[SuiteResult](suite => Try(Boon.runSuiteLike(suite)))
      }

      suiteResultTry match {
        case Failure(error) =>
          handleEvent(createErrorEvent(taskDef, error, timeTaken), eventHandler)
          statusLister.suiteFailed(s"could not load class: ${taskDef.fullyQualifiedName}", error, loggers)
        case Success(suiteResult) =>
          handleEvent(
            createEvent[SuiteResult](taskDef, suiteResultToStatus, suiteResult, timeTaken), eventHandler
          )
          statusLister.suiteResult(suiteResult, loggers)
      }

      Array.empty
  }

  private def runTimed[A](toRun: () => A): (Long, A) = {
      val startTime = System.currentTimeMillis()
      val result    = toRun()
      val endTime   = System.currentTimeMillis()
      val timeTaken = endTime - startTime
      (timeTaken, result)
  }

  private def suiteResultToStatus(sr: SuiteResult): Status =
    SuiteResult.suiteResultToSuiteState(sr) match {
      case SuiteState.Passed => Status.Success
      case SuiteState.Failed => Status.Failure
    }
}