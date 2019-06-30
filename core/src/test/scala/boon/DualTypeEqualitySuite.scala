package boon

import boon.model.Assertion
import boon.model.AssertionResultFailed
import boon.model.AssertionError
import boon.model.StringRep
import boon.model.AssertionName
import boon.model.AssertionTriple
import boon.model.AssertionResultPassed
import boon.model.SingleAssertionResult
import boon.data.NonEmptySeq
import boon.model.SingleTestResult
import boon.model.DeferredTest
import boon.model.TestName
import boon.BoonAssertions.Desc
import boon.BoonAssertions.Got
import boon.BoonAssertions.Expected
import boon.BoonAssertions.failWith
import syntax.collection._
import syntax.option._
import scala.util.matching.Regex

object DualityTypeEqualitySuite extends SuiteLike("DualityTypeEquality Suite") {

  private val t1 = test("equates different types successfully") {
    val equalSuccessTest = test("equates successfully") {
      "hello world" =>= 11 =>> (_.length =?= _ | "greeting length")
    }

    Boon.runTest(equalSuccessTest) match {
      case SingleTestResult(DeferredTest(TestName(name), _, _), NonEmptySeq(SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(aName), _, _))), _)) =>
        name  =?= "equates successfully" | "test name" and
        aName =?= "greeting length"      | "assertion name"

       case other => failWith(Expected("SingleTestResult with 1 test"), Got(other), Desc("test type"))
    }
  }

  private val t2 = test("equales different types unsuccessfully") {
    implicit val regexStringRep = StringRep.from[Regex](r => s"/${r}/")
    val equalUnsuccessfulTest = test("equates unsuccessfully") {
      raw"\d{3}".r =>= "A12B" =>> ((r, v) =>  some_?(r.findFirstIn(v))(_ =?= "123" | "regex match"))
    }

    Boon.runTest(equalUnsuccessfulTest) match {
      case SingleTestResult(DeferredTest(TestName(name), _, _), NonEmptySeq(SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(aName), _, context, location), errors))), _)) =>
        name =?= "equates unsuccessfully" | "test name"                    and
        aName =?= "expect Some" | "assertion name"                         and
        context =?= Map("values" -> "(/\\d{3}/, \"A12B\")")   | "context"  and
        location.line =?= 42 | "error location"                            and
        positional(errors, "assertion.errors")(one(_.contains("expected Some") | "reason"))

       case other => failWith(Expected("SingleTestResult with 1 test"), Got(other), Desc("test type"))
    }
  }

  override val tests = oneOrMore(t1, t2)
}