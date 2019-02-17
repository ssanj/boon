package boon.sbt

import sbt.testing.Runner
import sbt.testing.Task
import sbt.testing.TaskDef

final class BoonRunner(
  val args: Array[String],
  val remoteArgs: Array[String],
  classLoader: ClassLoader)
  extends Runner {

  override def tasks(list: Array[TaskDef]): Array[Task] = {
    list.map(new BoonTask(_, classLoader))
  }

  override def done(): String = ""

}
