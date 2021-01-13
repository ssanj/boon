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
import syntax.collection.positional
import internal.instances._

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

      sequentially(
        saturdayMenu.length =?= 10                                                                     | "length"   and
        saturdayMenu.head   =?= "Chocolate cake"                                                       | "head"     and
        saturdayMenu.last   =?= "Slice of watermelon"                                                  | "last"     and
        saturdayMenu.contains("Pickle") >> (one(s"Could not find 'Pickle' in $saturdayMenu"), Replace) | "contains"
      )
    }

    Boon.runTest(tx) match {
      case CompositeTestResult(AllPassed(TestName(name), passed)) =>
        name =?= "NonEmptySeq test" | "test name" and
        positional(passed, "passed"){
          oneOrMore(
            assertSequentialPass("length"),
            assertSequentialPass("head"),
            assertSequentialPass("last"),
            assertSequentialPass("contains")
          )
        }

      case other => failWith(Expected("CompositeTestResult"), Got(other), Desc("test result type"))
    }
  }

  override val tests = oneOrMore(t1)
}