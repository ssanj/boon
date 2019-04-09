package boon

import model.IsEqual
import model.IsNotEqual
import syntax._

object BoonSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("create testable") {
    %@(Boon.testable[Int](defer(10), defer(20), IsEqual).run) { testable =>
      val value1 = testable.value1.run
      val value2 = testable.value2.run

      value1.asInstanceOf[Int] =?= 10                  | "value1"        and
      value2.asInstanceOf[Int] =?= 20                  | "value2"        and
      testable.equalityType =?= IsEqual                | "equality type" and
      testable.equality.eql(value1, value2) =?= false  | "equality"      and
      testable.difference.diff(value1, value2) =?= one("10 != 20") | "diff"
    }
  }

  private val t2 = test("with EqualityTypes") {
    %@(Boon.testable[String](defer("Blah"), defer("Blee"), IsNotEqual).run) { testable =>
      testable.equalityType =?= IsNotEqual | "is not equal"
    } and %@(Boon.testable[Char](defer('c'), defer('d'), IsEqual).run) { testable =>
      testable.equalityType =?= IsEqual | "is equal"
    }
  }

  override val tests = oneOrMore(t1, t2)
}