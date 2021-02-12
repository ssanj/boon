package boon.sbt

import sbt.testing.TaskDef
import sbt.testing.SuiteSelector
import sbt.testing.{Event => SbtEvent}
import sbt.testing.Fingerprint
import sbt.testing.EventHandler
import sbt.testing.OptionalThrowable
import sbt.testing.Status
import sbt.testing.Selector

object Event {

  sealed abstract class BoonSbtEvent(taskDef: TaskDef, timeTakenMs: Long) extends SbtEvent {

    override def fullyQualifiedName(): String = taskDef.fullyQualifiedName()

    override def throwable(): OptionalThrowable = new OptionalThrowable()

    override def selector(): Selector = taskDef.selectors.headOption.getOrElse(new SuiteSelector)

    override def fingerprint(): Fingerprint = taskDef.fingerprint

    override def duration(): Long = timeTakenMs
  }

  def createEvent[A](taskDef: TaskDef, resultToStatus: A => Status, result: A, timeTakenMs: Long): SbtEvent =
    new BoonSbtEvent(taskDef, timeTakenMs) {
      override def status(): Status = resultToStatus(result)
    }

  def createErrorEvent(taskDef: TaskDef, error: Throwable, timeTakenMs: Long): SbtEvent =
    new BoonSbtEvent(taskDef, timeTakenMs) {

      override def throwable(): OptionalThrowable = new OptionalThrowable(error)

      override def status(): Status = Status.Error
    }

  def handleEvent(event: SbtEvent, eventHandler: EventHandler): Unit = {
    eventHandler.handle(event)
  }
}
