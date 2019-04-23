package boon
package model

import syntax._
import option._
import internal.instances._

object PredicateSuite extends SuiteLike("Predicate Suite") {

  private val t1 = test("Create AssertionData") {
    val deferInt1 = defer(10)
    val deferInt2 = defer(20)
    val intPair = (deferInt1, deferInt2)
    val intPredicate = new Predicate[Int](intPair, IsEqual, None)
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
          loc.line =?= 15 | "line" and
          some_?(loc.fileName)(_ =?= "PredicateSuite.scala" | "fileName") and
          some_?(loc.filePath)(_.endsWith("PredicateSuite.scala") | "filePath")
        }
      }
    } and none_?(intPredicate.overrideErrors)( pass | "no errors overridden")
  }

  private val t2 = test("Override error messages") {
    val deferString = defer("some String")
    val stringPair = (deferString, deferString)
    val stringPredicate = new Predicate[String](stringPair, IsEqual, None)

    none_?(stringPredicate.overrideErrors)(pass | "create without errors overridden") and
    %@(stringPredicate >> oneOrMore("error1", "error2")) { pred1 =>
      some_?(pred1.overrideErrors)(_ =?= oneOrMore("error1", "error2") | "having errors overridden")
    }
  }

  private val t3 = test("Create Predicate with error messages") {
    val deferChar1 = defer('c')
    val deferChar2 = defer('d')
    val charPair = (deferChar1, deferChar2)
    val charPredicate = new Predicate[Char](charPair, IsNotEqual, oneOrMore("err1", "err2", "err3").some)

    some_?(charPredicate.overrideErrors)(_ =?= oneOrMore("err1", "err2", "err3") | "create with errors overridden")
  }

  override val tests = oneOrMore(t1, t2, t3)
}