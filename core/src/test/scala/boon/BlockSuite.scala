package boon

import boon.model.AssertionData
import boon.model.Test
import boon.BoonAssertions.Desc
import boon.BoonAssertions.Got
import boon.BoonAssertions.Expected
import boon.BoonAssertions.failWith
import syntax.collection.positional
import boon.model.AssertionResult
import boon.model.SingleAssertionResult
import boon.model.AssertionResultPassed
import boon.model.Independent
import boon.model.TestName
import boon.model.DeferredTest
import boon.model.SingleTestResult
import boon.internal.instances._

object BlockSuite extends SuiteLike("Block Test Suite") {

  private val t1 = test("multiple assertions") {
    val tx = test("my block test") {
      %@(List(1,2,3)) { l =>
        l.length =?= 3              | "length" and
        l.reverse =?= List(3, 2, 1) | "reverse"
      }
    }

    Boon.runTest(tx) match {
      case SingleTestResult(DeferredTest(TestName(name), assertions, Independent), assertionResults) =>
        name =?= "my block test" | "test name" and
        positional(assertions, "assertions"){
          oneOrMore(_.name.value =?= "length"  | "assertion", 
                    _.name.value =?= "reverse" | "assertion")
        } and assertionResults.toSeq.forall {
          case SingleAssertionResult(AssertionResultPassed(_)) =>  true
          case _ => false
        } | ("all passed",
             "results" -> assertionResults.map(ar => AssertionResult.assertionNameFromResult(ar).value).mkString(","))

      case other => failWith(Expected("SingleTestResult"),  Got(other), Desc("test type"))
    }
  }

  private def createSingleTest(testName: String, prefixOp: Option[String]): Test = {
    test(testName) {
      prefixOp.fold({
        %@("testing") { testing =>
          testing.length =?= 7 | "length"
        }
      })({ prefix =>
        %@("testing", prefix) { testing =>
          testing.length =?= 7 | "length"
        }
      })
    }
  }

  private val t2 = test("prepends prefix") {
    val tx = createSingleTest("my other block test", Some("test"))
    assertSuccess(tx, "my other block test", "test.length")
  }

  private val t3 = test("without prefix") {
    val tx = createSingleTest("my other other block test", None)
    assertSuccess(tx, "my other other block test", "length")
  }

  private def assertSuccess(test: Test, testName: String, assertionName: String): AssertionData = {
    Boon.runTest(test) match {
      case SingleTestResult(DeferredTest(TestName(name), assertions, Independent), _) =>
        name =?= testName | "test name" and
        positional(assertions, "block")(one(_.name.value =?= assertionName  | "assertion"))
      case other => failWith(Expected("SingleTestResult"),  Got(other), Desc("test type"))
    }
  }

  override val tests = oneOrMore(t1, t2, t3)
}