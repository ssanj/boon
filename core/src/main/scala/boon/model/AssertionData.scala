package boon
package model

import boon.data.NonEmptySeq

final case class AssertionData(assertions: NonEmptySeq[Assertion]) {
  def and(other: AssertionData): AssertionData = AssertionData(assertions.concat(other.assertions))

  def seq(): TestData = TestData(assertions, Sequential)

  def ind(): TestData = TestData(assertions, Independent)
}

