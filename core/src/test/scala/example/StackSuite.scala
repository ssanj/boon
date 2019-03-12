package example

import boon._
import syntax._

object StackSuite extends SuiteLike("NonEmptyStack"){

  import example.Stack

  private def noStack[A]: Option[Stack[A]] = None: Option[Stack[A]]

  private val test1 = test("Pop") {
    val stack1 = new Stack[Int](NonEmptySeq.nes(0))
    val stack2 = stack1.push(1)
    val stack3 = stack2.push(2)

    {
      val pop3 = stack3.pop()

      pop3._1  =?= 2                        | "pop3 returns 2 as value"     and
      pop3._2  =?= Some(stack2)             | "pop3 returns stack2 as stack" and
      pop3     =?= Tuple2(2, Some(stack2))  | "pop3 pair" sequentially "pop3 stuff"
    } and
    stack2.pop()._1  =?= 1                       | "popping stack2 returns 1 as value" and
    stack2.pop()._2  =?= Some(stack1)            | "popping stack2 returns stack1 as stack" and
    stack1.pop()     =?= Tuple2(0, noStack[Int]) | "popping stack1 returns 0 as value and no further stacks"
  }

  override def tests = NonEmptySeq.nes(test1)
}