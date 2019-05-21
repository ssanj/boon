package boon
package model

import boon.data.NonEmptySeq
import boon.BoonAssertions.failWith
import boon.BoonAssertions.Desc
import boon.BoonAssertions.Got
import boon.BoonAssertions.Expected
import syntax._
import option._
import internal.instances._

object PredicateSuite extends SuiteLike("Predicate Suite") {

  private val t1 = test("Create AssertionData") {
    val deferInt1 = defer(10)
    val deferInt2 = defer(20)
    val intPair = (deferInt1, deferInt2)
    val intPredicate = new Predicate[Int](intPair, IsEqual)
    val assertionData = intPredicate | "Int predicate"

    %@(assertionData.assertions) { assertions =>
      assertions.length =?= 1 | "no of assertions" and
      %@(assertions.head, "assertion"){ a1 =>
        a1.name.value =?= "Int predicate" | "name" and
        a1.context =?= noContext | "context" and %@(a1.testable.run, "testable") { testable =>
          val value1 = testable.value1.run
          val value2 = testable.value2.run

          value1.asInstanceOf[Int] =?= 10                              | "value1"     and
          value2.asInstanceOf[Int] =?= 20                              | "value2"     and
          testable.equality.eql(value1, value2) =?= false              | "equality"   and
          testable.difference.diff(value1, value2) =?= one("10 != 20") | "difference" and
          testable.equalityType =?= IsEqual                            | "equalityType"
        } and %@(a1.location, "a1.loc") { loc =>
          loc.line =?= 20 | "line" and
          some_?(loc.fileName)(_ =?= "PredicateSuite.scala" | "fileName") and
          some_?(loc.filePath)(_.endsWith("PredicateSuite.scala") | "filePath")
        }
      }
    }
  }

  private val t2 = test("overriding difference") {
    val errors = oneOrMore("one", "two", "three", "a one-two-three")
    val diff = Difference.fromResult[String](errors)
    val testPredicate = test("test predicate override diffs") {
      "Hello" =?= "Yellow" |? ("greeting", diff)
    }

    Boon.runTest(testPredicate) match {
      case SingleTestResult(_, NonEmptySeq(SingleAssertionResult(AssertionResultFailed(AssertionError(_, assertionErrors))), _)) =>
        pass                       | "valid test result" and
        assertionErrors =?= errors | "expected diffs"
      case other => failWith(Expected("SingleAssertionResult/AssertionResultFailed/AssertionError"), Got(other), Desc("valid test result"))
    }
  }

  private val t3 = test("overriding error messages") {
    val errors = oneOrMore("four", "five", "six")
    val testPredicate = test("test predicate override error messages") {
      ("blue" =?= "Red") >> errors | "Colours"
    }

    Boon.runTest(testPredicate) match {
      case SingleTestResult(_, NonEmptySeq(SingleAssertionResult(AssertionResultFailed(AssertionError(_, assertionErrors))), _)) =>
        pass                       | "valid test result" and
        assertionErrors =?= errors | "expected error messages"
      case other => failWith(Expected("SingleAssertionResult/AssertionResultFailed/AssertionError"), Got(other), Desc("valid test result"))
    }
  }

  override val tests = oneOrMore(t1, t2, t3)
}