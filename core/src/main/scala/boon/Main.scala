package boon

//Lets you run your test as a executable
trait Main { self: SuiteLike =>

  //override to change configuration such as printers etc
  def defaultConfig: ReplConfig = ReplConfig.defaultConfig

  def main(args: Array[String]): Unit = {
    REPL.runSuites(self)(defaultConfig)
  }
}