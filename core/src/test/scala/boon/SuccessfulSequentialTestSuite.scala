package boon

import boon.data.NonEmptySeq
import boon.model.TestName
import boon.model.AllPassed
import boon.model.CompositeTestResult
import BoonAssertions.assertSequentialPass
import BoonAssertions.failWith
import BoonAssertions.Expected
import BoonAssertions.Got
import BoonAssertions.Desc

object SuccessfulSequentialTestSuite extends SuiteLike("BoonSuite") {

  private val t1 = test("successful test - sequential") {

    val tx = test("NonEmptySeq test") {
      val saturdayMenu: NonEmptySeq[String] =
        oneOrMore(
          "Chocolate cake",
          "Ice cream cone",
          "Pickle",
          "Swiss cheese",
          "Slice of Salami",
          "Lollipop",
          "Cherry Pie",
          "Sausage",
          "Cup cake",
          "Slice of watermelon"
        )

      saturdayMenu.length =?= 10                                                            | "length"   and
      saturdayMenu.head   =?= "Chocolate cake"                                              | "head"     and
      saturdayMenu.last   =?= "Slice of watermelon"                                         | "last"     and
      saturdayMenu.contains("Pickle") >> one(s"Could not find 'Pickle' in $saturdayMenu")   | "contains" seq()
    }

    Boon.runTest(tx) match {
      case CompositeTestResult(AllPassed(TestName(name), passed)) =>
        name =?= "NonEmptySeq test" | "test name"   and
        passed.length =?= 4   | "no of assertions"  and %@(passed.toSeq) { p =>
          assertSequentialPass("length")(p(0))      and
          assertSequentialPass("head")(p(1))        and
          assertSequentialPass("last")(p(2))        and
          assertSequentialPass("contains")(p(3))
        }

      case other => failWith(Expected("CompositeTestResult"), Got(other), Desc("test result type"))
    }
  }

  override val tests = oneOrMore(t1)
}