package boon

import boon.data.NonEmptySeq
import model.IsEqual
import model.IsNotEqual
import model.SingleAssertionResult
import model.AssertionResultPassed
import model.AssertionTriple
import model.AssertionName
import model.AssertionResultFailed
import model.Assertion
import model.AssertionError
import model.AssertionResultThrew
import model.AssertionThrow

import org.scalacheck.Properties
import org.scalacheck.Prop
import org.scalacheck.Prop._
import org.scalacheck._

object BoonRunAssertionProps extends Properties("Boon#runAssertion") {

  private implicit val throwableArb =
    Arbitrary[Throwable]{
      for {
        error <- Arbitrary.arbitrary[String]
        ex    <- Gen.oneOf(new RuntimeException(error),
                           new IllegalStateException(error),
                           new IllegalArgumentException(error))
      } yield ex
    }

  property("Assertion that is equal") = Prop.forAll { (name: String, v1: Int, ctx: Map[String, String]) =>
    val assertion = Boon.defineAssertion[Int](name, (defer(v1), defer(v1)), IsEqual, ctx)
    val result = Boon.runAssertion(assertion)

    result match {
      case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(name), context, location))) =>
        (name == name)        :| "name"    &&
        (context == ctx)      :| "context" &&
        (location.line == 34) :| "location line"
      case other => falsified :| (s"Expected SingleAssertionResult(AssertionResultPassed) but got $other")
    }
  }

  property("Assertion that is not equal") = Prop.forAll { (name: String, v1: Int, ctx: Map[String, String]) =>
    val assertion = Boon.defineAssertion[Int](name, (defer(v1), defer(v1 + 1)), IsNotEqual, ctx)
    val result = Boon.runAssertion(assertion)

    result match {
      case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(name), context, location))) =>
        (name == name)        :| "name"    &&
        (context == ctx)      :| "context" &&
        (location.line == 47) :| "location line"
      case other => falsified :| (s"Expected SingleAssertionResult(AssertionResultPassed) but got $other")
    }
  }

  property("Assertion that fails") = Prop.forAll { (name: String, v1: Int, ctx: Map[String, String]) =>
    val v2 = v1 + 1
    val assertion = Boon.defineAssertion[Int](name, (defer(v1), defer(v2)), IsEqual, ctx)
    val result = Boon.runAssertion(assertion)

    result match {
      case SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(name), _, context, location), NonEmptySeq(error, _)))) =>
        (name == name)           :| "name"          &&
        (context == ctx)         :| "context"       &&
        (location.line == 61)    :| "location line" &&
        (error == s"$v1 != $v2") :| "error message"
      case other => falsified :| (s"Expected SingleAssertionResult(AssertionResultFailed) but got $other")
    }
  }

  property("Assertion that throws") = Prop.forAll { (name: String, v1: Int, ctx: Map[String, String], ex: Throwable) =>
    val assertion = Boon.defineAssertion[Int](name, (defer(v1), defer(throw ex)), IsEqual, ctx)
    val result = Boon.runAssertion(assertion)

    result match {
      case SingleAssertionResult(AssertionResultThrew(AssertionThrow(AssertionName(name), throwable, location))) =>
        (name == name)                          :| "name"          &&
        (location.line == 75)                   :| "location line" &&
        (throwable.getMessage == ex.getMessage) :| "error message"
      case other => falsified :| (s"Expected SingleAssertionResult(AssertionResultThrew) but got $other")
    }
  }
}