package boon
package model

import boon.data.NonEmptySeq

final case class AssertionData(assertions: NonEmptySeq[Assertion]) {
  def and(other: AssertionData): AssertionData = AssertionData(assertions.concat(other.assertions))

  def context(ctx: Map[String, String]): AssertionData = {
    val assertionsWithContext = assertions.map { assertion =>
      assertion.copy(context = assertion.context ++ ctx)
    }

    AssertionData(assertionsWithContext)
  }

  def label(f: AssertionName => AssertionName): AssertionData = {
    this.copy(assertions = assertions.map(a => a.copy(name = f(a.name))))
  }

  def seq(): TestData = TestData(assertions, Sequential)

  def ind(): TestData = TestData(assertions, Independent)
}

