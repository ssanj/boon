package boon.syntax

import boon.model.AssertionFailureDouble
import boon.model.AssertionData
import boon.model.AssertionResult
import boon.model.DeferredTest
import boon.model.TestName
import boon.model.SingleTestResult
import boon.BoonAssertions.Desc
import boon.BoonAssertions.Got
import boon.BoonAssertions.Expected
import boon._
import syntax.either._
import syntax.option._
import syntax.nes._
import BoonAssertions.failWith
import BoonAssertions.nesElements5
import model.AssertionResult.assertionNameFromResult
import model.AssertionResult.getErrors

object NesSuite extends SuiteLike("NonEmptySeq Suite") {

  private type E = Either[String, Int]

  private val t1 = test("positional arguments failure test") {
    val positionalTest = test("positional failure test") {
      val l1 = oneOrMore(success(1), error("some error"), success(3), success(4))//one error
      val assertions = oneOrMore(isRight(_:E), isRight(_:E), isRight(_:E), isRight(_:E))//expect all success

      positional[E](l1)(assertions)
    }

    Boon.runTest(positionalTest) match {
      case SingleTestResult(DeferredTest(TestName(testName), _, _), assertionResults) =>
        testName =?= "positional failure test" | "test name"        and
        nesElements5[AssertionResult](assertionResults, "assertionResults")(
          assertionLength _,
          assertionIsRight _,
          assertionIsLeft _,
          assertionIsRight _,
          assertionIsRight _
        )
        
      case other => failWith(Expected("SingleTestResult/SingleAssertionResult/AssertionError"), Got(other), Desc("test type"))
    }
  }

  private val t2 = test("positional arguments success test") {
    val positionalTest = test("positional success test") {

      val l1 = oneOrMore(success(1), success(2), success(3), success(4))
      val assertions = oneOrMore(isRight(_:E), isRight(_:E), isRight(_:E), isRight(_:E))

      positional[E](l1)(assertions)
    }

    Boon.runTest(positionalTest) match {
      case SingleTestResult(DeferredTest(TestName(testName), _, _), assertionResults) =>
        testName =?= "positional success test" | "test name"        and
        nesElements5[AssertionResult](assertionResults, "assertionResults")(
          assertionLength _,
          assertionIsRight _,
          assertionIsRight _,
          assertionIsRight _,
          assertionIsRight _
        )
        
      case other => failWith(Expected("SingleTestResult"), Got(other), Desc("test type"))
    }
  }

  private def error(value: String): Either[String, Int] = Left(value)

  private def success(value: Int): Either[String, Int] = Right(value)

  private def assertionLength(ar: AssertionResult): AssertionData = 
    assertionNameFromResult(ar).value =?= "match lengths" | "positional match length"

  private def assertionIsLeft(ar: AssertionResult): AssertionData = {
    some_?[AssertionFailureDouble](getErrors(ar)) { failureDouble =>
      failureDouble.name.value =?= "is Right" | "failed isRight assertion" and
      failureDouble.errors.head =?= "expected Right got: Left(\"some error\")"| "error message"
    }
  }

  private def assertionIsRight(ar: AssertionResult): AssertionData = 
    assertionNameFromResult(ar).value =?= "is Right" | "isRight assertion"

  override val tests = oneOrMore(t1, t2)
}