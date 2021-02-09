package boon
package model

import boon.data.NonEmptySeq

/** Represents a collection of [[boon.model.Assertion]]s and its
 * combinators.
 *
 * @param assertions The [[boon.model.Assertion]]s that make up this object
 */
final case class AssertionData(assertions: NonEmptySeq[Assertion]) {

  /** Combines this AssertionData with another */
  def and(other: AssertionData): AssertionData = AssertionData(assertions.concat(other.assertions))

  /** Appends a context to every Assertion in this object */
  def context(ctx: Map[String, String]): AssertionData = {
    val assertionsWithContext = assertions.map { assertion =>
      assertion.copy(context = assertion.context ++ ctx)
    }

    AssertionData(assertionsWithContext)
  }

  def ctx(firstCtx: (String, String), rest: (String, String)*): AssertionData = context(Map((firstCtx +: rest):_*))

  /** Updates the label of all Assertions in this object */
  def label(f: AssertionName => AssertionName): AssertionData = {
    this.copy(assertions = assertions.map(a => a.copy(name = f(a.name))))
  }

  /** Combines the Assertions in this object sequentially
   * so they fail on the first Assertion error.
   */
  //TODO: rename to stopOnFailure
  def stopOnFailure(): TestData = TestData(assertions, StopOnFailure)

  /** Combines the Assertions in this object independently
   * so they continue to run irrespective of previous
   * Assertion failures.
   */
   //TODO: rename to continueOnFailure
  def continueOnFailure(): TestData = TestData(assertions, ContinueOnFailure)
}

