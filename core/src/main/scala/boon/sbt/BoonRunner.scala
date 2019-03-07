package boon.sbt

import sbt.testing.Runner
import sbt.testing.Task
import sbt.testing.TaskDef

import boon.printers.PrinterSetting
import boon.printers.SimplePrinter

final class BoonRunner(
  val args: Array[String],
  val remoteArgs: Array[String],
  classLoader: ClassLoader)
  extends Runner {

  //use default printer for now, change to use from args
  override def tasks(list: Array[TaskDef]): Array[Task] = {
    list.map(new BoonTask(_,
                          classLoader,
                          (so, c, print) =>
                            SimplePrinter(so, PrinterSetting.defaults(c), print)))
  }

  override def done(): String = ""

}
