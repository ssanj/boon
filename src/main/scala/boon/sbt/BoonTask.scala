package boon.sbt

import sbt.testing.Task
import sbt.testing.TaskDef
import sbt.testing.Event
import sbt.testing.EventHandler
import sbt.testing.Fingerprint
import sbt.testing.Logger
import sbt.testing.OptionalThrowable
import sbt.testing.Status
import sbt.testing.Selector

import boon.Boon
import boon.Failed
import boon.Passed
import boon.SuiteLike
import boon.printers.SuiteOutput
import boon.SuiteResult

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import scala.reflect.runtime.universe._

final class BoonTask(val taskDef: TaskDef, cl: ClassLoader, printer: (SuiteOutput, Boolean) => String) extends Task {

  def tags(): Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
      val suiteTry = loadSuite(taskDef.fullyQualifiedName, cl)
      val startTime = System.currentTimeMillis()
      val suiteResultTry = suiteTry.flatMap[SuiteResult](suite => Try(Boon.runSuiteLike(suite)))
      val endTime = System.currentTimeMillis()
      val timeTaken = endTime - startTime

      suiteResultTry match {
        case Failure(error) =>
          handleEvent(createErrorEvent(error, timeTaken), eventHandler)
          logError(s"could not load class: ${taskDef.fullyQualifiedName}", error, loggers)
        case Success(suiteResult) =>
          val suiteOutput = SuiteOutput.toSuiteOutput(suiteResult)
          handleEvent(createEvent(suiteResult, timeTaken), eventHandler)
          logResult(suiteOutput, loggers)
      }

      Array.empty
  }

  private def logResult(suiteOutput: SuiteOutput, loggers: Array[Logger]): Unit = {
    loggers.foreach { log =>
      log.info(printer(suiteOutput, log.ansiCodesSupported))
    }
  }

  private def logError(message: String, error: Throwable, loggers: Array[Logger]): Unit = {
    loggers.foreach{ l =>
      l.error(message)
      l.trace(error)
    }
  }

  private def handleEvent(event: Event, eventHandler: EventHandler): Unit = {
    eventHandler.handle(event)
  }

  private def createEvent(result: SuiteResult, timeTakenMs: Long): Event = new Event {

    override def fullyQualifiedName(): String = taskDef.fullyQualifiedName()

    override def throwable(): OptionalThrowable = new OptionalThrowable()

    override def status(): Status = {
      Boon.suiteResultToPassable(result) match {
        case Passed => Status.Success
        case Failed => Status.Failure
      }
    }

    override def selector(): Selector = taskDef.selectors.head//Unsafe

    override def fingerprint(): Fingerprint = taskDef.fingerprint

    override def duration(): Long = timeTakenMs
  }

  private def createErrorEvent(error: Throwable, timeTakenMs: Long): Event = new Event {

    override def fullyQualifiedName(): String = taskDef.fullyQualifiedName()

    override def throwable(): OptionalThrowable = new OptionalThrowable(error)

    override def status(): Status = Status.Error

    override def selector(): Selector = taskDef.selectors.head//Unsafe

    override def fingerprint(): Fingerprint = taskDef.fingerprint

    override def duration(): Long = timeTakenMs
  }

  private def loadSuite(name: String, loader: ClassLoader): Try[SuiteLike] = {
    Try(reflectivelyInstantiateSuite(name, loader)).collect {
      case ref: SuiteLike => ref
    }
  }

  private def reflectivelyInstantiateSuite(className: String, loader: ClassLoader): Any = {
   val mirror = runtimeMirror(loader)
   val module = mirror.staticModule(className)
   mirror.reflectModule(module).instance.asInstanceOf[Any]
  }
}