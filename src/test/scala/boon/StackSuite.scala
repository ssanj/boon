package boon

import syntax._
import Boon.test
import example.Stack

object StackTests {

  private def noStack[A]: Option[Stack[A]] = None: Option[Stack[A]]

  private [boon] val test1 = test("A Stack should pop values in last-in-first-out order") {
    val stack1 = new Stack[Int](NonEmptySeq.nes(0))
    val stack2 = stack1.push(1)
    val stack3 = stack2.push(2)

    {
      val pop3 = stack3.pop()

      ( pop3._1 =?= 2            | "pop3 returns 2 as value" )     &
      ( pop3._2 =?= Some(stack2) | "pop3 returns stack2 as stack")

    } &
    ( stack2.peek =?= 1 | "peeking") &
    ( stack2.pop()._1  =?= 1                       | "popping stack2 returns 1 as value" )                      &
    ( stack2.pop()._2  =?= Some(stack1)            | "popping stack2 returns stack1 as stack" )                 &
    ( stack1.pop()     =?= Tuple2(0, noStack[Int]) | "popping stack1 returns 0 as value and no further stacks")
  }
}

import StackTests._

final class StackSuite extends SuiteLike("Stack") {
  override def tests = NonEmptySeq.nes(test1)
}

object StackSuite {

  def main(args: Array[String]): Unit =  {
    val s1 = new StackSuite
    val suiteResult = Boon.runSuiteLike(s1)
    val suiteOutput = SuiteOutput.toSuiteOutput(suiteResult)
    println(SimplePrinter.print(suiteOutput, SuiteOutput.defaultPrinterSetting(true)))
  }
}