package boon
package model

import boon.internal.instances._

object AssertionDataSuite extends SuiteLike("AssertionData Suite") {

  private val assertionData = true | "truism"

  private val t1 = test("Create StopOnFailure TestData") {
    val testData = assertionData.stopOnFailure()
    testData.assertions =?= assertionData.assertions | "assertions" and
    testData.combinator =?= StopOnFailure | "combinator"
  }

  private val t2 = test("Create ContinueOnFailure TestData") {
    val testData = assertionData.continueOnFailure()
    testData.assertions =?= assertionData.assertions | "assertions" and
    testData.combinator =?= ContinueOnFailure | "combinator"
  }

  private val t3 = test("append Assertions") {
    val assertionData1 = true  | "truism"
    val assertionData2 = false | "falsism"

    val combinedAssertions = assertionData1.assertions.concat(assertionData2.assertions)

    assertionData1.and(assertionData2) =?= AssertionData(combinedAssertions) | "and"
  }

  override val tests = oneOrMore(t1, t2, t3)
}