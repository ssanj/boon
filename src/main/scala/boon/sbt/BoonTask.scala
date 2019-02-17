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
import boon.SuiteOutput
import boon.SuiteResult

import scala.util.Try

final class BoonTask(val taskDef: TaskDef, cl: ClassLoader, printer: (SuiteOutput, Boolean) => String) extends Task {

  def tags(): Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    val suiteOp = loadSuite(taskDef.fullyQualifiedName, cl)
    //look into async execution
    suiteOp.fold(println(s"could not load class: ${taskDef.fullyQualifiedName}")) { suite =>
      val startTime = System.currentTimeMillis()
      val suiteResult = Boon.runSuiteLike(suite)
      val endTime = System.currentTimeMillis()

      handleEvent(createEvent(suiteResult, endTime - startTime), eventHandler)
      val suiteOutput = SuiteOutput.toSuiteOutput(suiteResult)
      logResult(suiteOutput, loggers)
    }

    Array.empty
  }

  private def logResult(suiteOutput: SuiteOutput, loggers: Array[Logger]): Unit = {
    loggers.foreach { log =>
      log.info(printer(suiteOutput, log.ansiCodesSupported))
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

  private def loadSuite(name: String, loader: ClassLoader): Option[SuiteLike] = {
    Try(reflectivelyInstantiateSuite(name, loader)).toOption.collect { case ref: SuiteLike => ref }
  }

  private def reflectivelyInstantiateSuite(className: String, loader: ClassLoader): Any = {
    //Use Scala reflection instead
    Class.forName(className, true, loader).getConstructor().newInstance()
  }
}