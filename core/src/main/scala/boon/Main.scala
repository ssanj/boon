package boon

//Lets you run your test as a executable
trait Main { self: SuiteLike =>

  def main(args: Array[String]): Unit = {
    REPL.runSuites(self)
  }
}