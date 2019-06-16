package boon

import boon.model.Test
import boon.model.AssertionData
import boon.model.SingleAssertionResult
import boon.model.DeferredTest
import boon.model.SingleTestResult
import boon.model.AssertionName
import boon.model.AssertionThrow
import boon.model.AssertionResultThrew
import boon.BoonAssertions.Desc
import boon.BoonAssertions.Got
import boon.BoonAssertions.Expected
import boon.model.TestName
import boon.BoonAssertions.failWith
import syntax.nes.nesElements1
import syntax.exception._
import syntax.option.none

object BlockWithExceptionSuite extends SuiteLike("Block Test Suite") {

  private val t1 = test("exception in block definition") {
    val tx = test("my exceptional test") {
      %@(blows()) { _ =>
        true | "truism"
      }
    }

    assertBlockException(tx, "assertion @ (BlockWithExceptionTestSuite.scala:24) !!threw an Exception!!")
  }

  private val t2 = test("exception in block") {
    val tx = test("my exceptional test") {
      %@(()) { _ =>
        blows()
        true | "truism"
      }
    }

    assertBlockException(tx, "assertion @ (BlockWithExceptionTestSuite.scala:34) !!threw an Exception!!")
  }

  private val t3 = test("exception without prefix and without location") {
    val tx = test("my exceptional test") {
      %@(()) { _ =>
        blows()
        true | "truism"
      }(SourceLocation(none[String], none[String], 45))
    }

    assertBlockException(tx, "assertion @ (-:45) !!threw an Exception!!")
  }

  private val t4 = test("exception with prefix and without location") {
    val tx = test("my exceptional test") {
      %@((), "ladeeda") { _ =>
        blows()
        true | "truism"
      }(SourceLocation(none[String], none[String], 56))
    }

    assertBlockException(tx, "assertion @ ladeeda (-:56) !!threw an Exception!!")
  }

  private def blows(): Unit = throw new IllegalStateException("blows!")

  private def assertBlockException(test: Test, errorMessage: String): AssertionData = {
    Boon.runTest(test) match {
      case SingleTestResult(DeferredTest(TestName(testName), _, _), assertionResults) =>
        testName =?= "my exceptional test" | "test name" and
        nesElements1(assertionResults, "assertionResult") {
          case SingleAssertionResult(AssertionResultThrew(AssertionThrow(AssertionName(assertionName), error, _))) =>
            assertionName =?= errorMessage | "assertion name" and
            error =!=[IllegalStateException](_ =?= "blows!" | "assertion error")
          case other => failWith(Expected("SingleAssertionResult/AssertionResultFailed"),  Got(other), Desc("assertion result type"))
        }

      case other => failWith(Expected("SingleTestResult"),  Got(other), Desc("test type"))
    }
  }

  override val tests = oneOrMore(t1, t2, t3, t4)
}