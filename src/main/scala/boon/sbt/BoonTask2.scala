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

// import boon.Boon
// import boon.Failed
// import boon.Passed
import boon.SuiteLike
// import boon.printers.SuiteOutput
// import boon.SuiteResult

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.runtime.universe._

object BoonTask2 {
  implicit val EC: ExecutionContext = ExecutionContext.global
}

final class BoonTask2(val taskDef: TaskDef, cl: ClassLoader) extends Task {

  def tags(): Array[String] = Array.empty

  def execute(eventHandler: EventHandler, loggers: Array[Logger]): Array[Task] = {
    import BoonTask._

    val startTime = System.currentTimeMillis()
    val asyncExec =
      Future {

        val tryRun =
          for {
            suiteClass   <- loadSuite(taskDef.fullyQualifiedName, cl)
            dSuiteResult <-  Try(boon.printers.IncrementalOutput.run(suiteClass.suite))
          } yield {
            // log(dSuiteResult.suite.name.value, loggers)
            println(dSuiteResult.suite.name.value)
            dSuiteResult.testResults.foreach { tr =>
              println(tr.test.name.value)
              //log(tr.test.name.value, loggers)
              tr.assertionResults.foreach { dar =>
                val output = Try(dar.result.value().toString).getOrElse("- failed -")
                println(s"${dar.assertion.name.value} - ${output}")
                // log(output, loggers)
              }
            }
            ()
          }

          val endTime = System.currentTimeMillis()
          val timeTaken = endTime - startTime

           tryRun match {
            case Failure(error) =>
              handleEvent(createErrorEvent(error, timeTaken), eventHandler)
              logError(s"failed to run suite: ${taskDef.fullyQualifiedName}", error, loggers)
            case Success(_) =>
              handleEvent(createEvent(boon.Passed, timeTaken), eventHandler)
              log("done", loggers)
          }
      }

    //Should this be a max timeout?
    Await.ready(asyncExec, Duration.Inf)

    Array.empty[Task]
  }

  private def log(message: String, loggers: Array[Logger]): Unit = {
    // loggers.foreach { log =>
    //   log.info(message)
    // }
    println(message)
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

  private def createEvent(result: boon.Passable, timeTakenMs: Long): Event = new Event {

    override def fullyQualifiedName(): String = taskDef.fullyQualifiedName()

    override def throwable(): OptionalThrowable = new OptionalThrowable()

    override def status(): Status = {
      result match {
        case boon.Passed => Status.Success
        case boon.Failed => Status.Failure
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