package boon
package syntax

import boon.model.AssertionFailureDouble
import boon.model.AssertionData
import boon.model.AssertionResult
import boon.model.DeferredTest
import boon.model.TestName
import boon.model.SingleTestResult
import boon.BoonAssertions.Desc
import boon.BoonAssertions.Got
import boon.BoonAssertions.Expected
import boon.BoonAssertions.mapElements2
import boon._
import syntax.either._
import syntax.option._
import syntax.regex._
import syntax.nes._
import BoonAssertions.failWith
import nes.positional
import model.AssertionResult.assertionNameFromResult
import model.AssertionResult.getErrors
import model.internal.instances._

object NesSuite extends SuiteLike("NonEmptySeq Suite") {

  private type E = Either[String, Int]

  private val t1 = test("positional arguments failure test") {
    val positionalTest = test("positional failure test") {
      val l1 = oneOrMore(success(1), error("some error"), success(3), success(4))//one error
      val assertions = oneOrMore(isRight(_:E), isRight(_:E), isRight(_:E), isRight(_:E))//expect all success

      positional[E](l1, "elements")(assertions)
    }

    Boon.runTest(positionalTest) match {
      case SingleTestResult(DeferredTest(TestName(testName), _, _), assertionResults) =>
        testName =?= "positional failure test" | "test name"        and
        positional[AssertionResult](assertionResults, "assertionResults"){
          oneOrMore(
            assertionLength (4) _,
            assertionIsRight(0) _,
            failAssertionIsLeft (1) _,
            assertionIsRight(2) _,
            assertionIsRight(3) _
          )
        }
        
      case other => failWith(Expected("SingleTestResult/SingleAssertionResult/AssertionError"), Got(other), Desc("test type"))
    }
  }

  private val t2 = test("positional arguments success test") {
    val positionalTest = test("positional success test") {

      val l1 = oneOrMore(success(1), success(2), success(3), success(4))
      val assertions = oneOrMore(isRight(_:E), isRight(_:E), isRight(_:E), isRight(_:E))

      positional[E](l1, "elements")(assertions)
    }

    Boon.runTest(positionalTest) match {
      case SingleTestResult(DeferredTest(TestName(testName), _, _), assertionResults) =>
        testName =?= "positional success test" | "test name"        and
        positional[AssertionResult](assertionResults, "assertionResults"){
          oneOrMore(
            assertionLength (4) _,
            assertionIsRight(0) _,
            assertionIsRight(1) _,
            assertionIsRight(2) _,
            assertionIsRight(3) _
          )
        }
        
      case other => failWith(Expected("SingleTestResult"), Got(other), Desc("test type"))
    }
  }

  private val t3 = test("positional assertions are less than the number of elements") {
    val positionalTest = test("positional assertions are less than elements") {
      val l1 = oneOrMore(success(1), success(2), success(3), success(4))
      val assertions = oneOrMore(isRight(_:E), isRight(_:E))

      positional[E](l1, "elements")(assertions)
    }

    Boon.runTest(positionalTest) match {
      case SingleTestResult(DeferredTest(TestName(testName), _, _), assertionResults) =>
        testName =?= "positional assertions are less than elements" | "test name" and
        positional[AssertionResult](assertionResults, "assertionResults"){
          oneOrMore(
            ar => assertionLength (2)(ar) and assertionLengthMustFail(ar),
            assertionIsRight(0) _,
            assertionIsRight(1) _
          )
        }
        
      case other => failWith(Expected("SingleTestResult"), Got(other), Desc("test type"))
    }    
  }

  private def error(value: String): Either[String, Int] = Left(value)

  private def success(value: Int): Either[String, Int] = Right(value)

  private def assertionLength(length: Int)(ar: AssertionResult): AssertionData = 
    assertionNameFromResult(ar).value =?= s"elements has length of $length" | "positional match length"

  private def assertionLengthMustFail(ar: AssertionResult): AssertionData = {
    some_?[AssertionFailureDouble](getErrors(ar)){ afd =>
      positional(afd.errors, "errors"){
        oneOrMore(
          _ =?= "length of elements is different to assertions" | "must fail length check",
          _ =?= "elements length: 4" | "length of elements",
          _ =?= "assertions length: 2" | "length of assertions"
        )
      }
    }
  }  

  private def failAssertionIsLeft(index: Int)(ar: AssertionResult): AssertionData = {
    some_?[AssertionFailureDouble](getErrors(ar)) { failureDouble =>
      failureDouble.assertion.name.value =?= s"elements(${index}).is Right" | "failed isRight assertion" and
      positional[String](failureDouble.errors, "errors"){
        one(_ =?= "expected Right got: Left(\"some error\")"| "error message")
      } and 
      mapElements2(failureDouble.context, "ctx")(
        (k, v) => k =?= s"expected value at (${index})" | "key 1" and v =?= "Left(\"some error\")" | "value 1",
        (k, v) => s"values" =?= k | "key 2" and v =^= s"""(${index}) -> Left(.)""".r | "value 2"
      )
    }
  }

  private def assertionIsRight(index: Int)(ar: AssertionResult): AssertionData = 
    assertionNameFromResult(ar).value =?= s"elements(${index}).is Right" | "isRight assertion"

  override val tests = oneOrMore(t1, t2, t3)
}