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

  /** Updates the label of all Assertions in this object */
  def label(f: AssertionName => AssertionName): AssertionData = {
    this.copy(assertions = assertions.map(a => a.copy(name = f(a.name))))
  }

  /** Combines the Assertions in this object sequentially
   * so they fail on the first Assertion error.
   */
  def seq(): TestData = TestData(assertions, Sequential)

  /** Combines the Assertions in this object independently
   * so they continue to run irrespective of previous 
   * Assertion failures.
   */

  def ind(): TestData = TestData(assertions, Independent)
}

