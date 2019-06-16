package boon

import boon.model.AssertionName
import boon.model.AssertionTriple
import boon.model.AssertionResult
import boon.model.AssertionResultPassed
import boon.model.SingleAssertionResult
import syntax.nes.nesElements4
import boon.BoonAssertions.Desc
import boon.BoonAssertions.Got
import boon.BoonAssertions.Expected
import boon.BoonAssertions.failWith
import boon.model.Independent
import boon.model.TestName
import boon.model.DeferredTest
import boon.model.SingleTestResult

object TableTestSuite extends SuiteLike("Table Test Suite") {

  private val t1 = test("success") {

    val additionTable =
      truthTable(
        (1, 1) -> tval(2),
        (1, 2) -> tval(3),
        (1, 3) -> tval(4),
        (2, 3) -> tval(5)
      )

    val tx = table[(Int, Int), Int]("addition table", additionTable)(x => x._1 + x._2)

    Boon.runTest(tx) match {
      case SingleTestResult(DeferredTest(TestName(name), assertions, Independent), assertionResults) =>
        name =?= "addition table" | "test name" and
        nesElements4(assertions, "truthTable")(
          _.name.value =?= "with (1, 1) is 2" | "val",
          _.name.value =?= "with (1, 2) is 3" | "val",
          _.name.value =?= "with (1, 3) is 4" | "val",
          _.name.value =?= "with (2, 3) is 5" | "val"
        ) and
        assertionResults.toSeq.forall {
          case SingleAssertionResult(AssertionResultPassed(_)) =>  true
          case _ => false
        } | ("all passed",
             "results" -> assertionResults.map(ar => AssertionResult.assertionNameFromResult(ar).value).mkString(","))

      case other => failWith(Expected("SingleTestResult"), Got(other), Desc("test type"))
    }
  }

  private val t2 = test("with failures") {
    val additionTable =
      truthTable(
        (1, 1) -> tval(2),
        (1, 2) -> tval(3),
        (1, 3) -> tval(2),
        (2, 3) -> tval(5)
      )

    val tx = table[(Int, Int), Int]("addition table", additionTable)(x => x._1 + x._2)

    Boon.runTest(tx) match {
      case SingleTestResult(DeferredTest(TestName(name), assertions, Independent), assertionResults) =>
        name =?= "addition table" | "test name" and
        nesElements4(assertions, "truthTable")(
          _.name.value =?= "with (1, 1) is 2" | "val",
          _.name.value =?= "with (1, 2) is 3" | "val",
          _.name.value =?= "with (1, 3) is 2" | "val",
          _.name.value =?= "with (2, 3) is 5" | "val"
        ) and
        %@(assertionResults.partition[String, String] {
          case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(name), _, _))) =>  Left[String, String](name)
          case other => Right[String, String](AssertionResult.assertionNameFromResult(other).value)
        }) { _.fold(onlyLeft => failWith(Expected("These.Both"), Got(s"OnlyLeft(${onlyLeft.strRep})"), Desc("result type")),
                    onlyRight => failWith(Expected("These.Both"), Got(s"OnlyRight(${onlyRight.strRep})"), Desc("result type")),
                    (passed, failed) =>
                      passed =?= oneOrMore("with (1, 1) is 2",
                                           "with (1, 2) is 3",
                                           "with (2, 3) is 5") | "passed" and
                      failed =?= oneOrMore("with (1, 3) is 2") | "failed"
              )
        }

      case other => failWith(Expected("SingleTestResult"), Got(other), Desc("test type"))
    }
  }

  override val tests = oneOrMore(t1, t2)
}