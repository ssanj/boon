package boon
package model

import syntax._
import BoonTypes._

object AssertionDataSuite extends SuiteLike("AssertionData Suite") {

  private val assertionData = true | "truism"

  private val t1 = test("Create Sequential TestData") {
    val testData = assertionData.seq()
    testData.assertions =?= assertionData.assertions | "assertions" and
    testData.combinator =?= Sequential | "combinator"
  }

  private val t2 = test("Create Independent TestData") {
    val testData = assertionData.ind()
    testData.assertions =?= assertionData.assertions | "assertions" and
    testData.combinator =?= Independent | "combinator"
  }

  private val t3 = test("append Assertions") {
    val assertionData1 = true  | "truism"
    val assertionData2 = false | "falsism"

    val combinedAssertions = assertionData1.assertions.concat(assertionData2.assertions)

    assertionData1.and(assertionData2) =?= AssertionData(combinedAssertions) | "and"
  }

  override val tests = oneOrMore(t1, t2, t3)
}