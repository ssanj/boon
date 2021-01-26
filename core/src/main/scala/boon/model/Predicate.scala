package boon
package model

import boon.data.NonEmptySeq
import Boon.defineAssertion

final class ContextAware[A](val pred: Predicate[A], name: => String) {

  /**
   * Add contextual data to the [[boon.model.Predicate]]
   */
  def |>(ctx: NonEmptySeq[(String, String)])(implicit loc: SourceLocation): AssertionData =
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, (pred.pair), pred.equalityType, Map(ctx.toSeq:_*))(pred.E, pred.D, loc)
      )
    )

  /**
   * Add contextual data to the [[boon.model.Predicate]]
   * Same as `|>`
   */
  def ctx(ctx: NonEmptySeq[(String, String)])(implicit loc: SourceLocation): AssertionData = |>(ctx)

  /**
   * customize all parameters  when constructing an AssertionData
   * @see [[boon.model.AssertDataParameter]]
   */
  def |?(params: AssertDataParameter[A]): AssertionData = {
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, pred.pair, pred.equalityType, params.ctx.toSeq.toMap)(params.equality, params.difference, params.loc)
      )
    )
  }

  def toAssertionData(implicit loc: SourceLocation): AssertionData =
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, (pred.pair), pred.equalityType, Map.empty)(pred.E, pred.D, loc)
      )
    )

}

/**
 * Container for all customisable parameters to a [[boon.model.AssertionData]] instance.
 * @type A types compared when creating a Predicate
 * @param difference How to display the differences between two instances of type `A`
 * @param equality How to equate two instances of type `A`
 * @param ctx Any contextual data to display on assertion failure
 * @param loc Source location where the assertion error should occur (if any)
 */
final class AssertDataParameter[A](val difference: Difference[A], val equality: Equality[A], val ctx: Map[String, String], val loc: SourceLocation)

final class DiffContentParameter[A](diffContentInternal: => NonEmptySeq[String], val mod: DifferenceMod) {
  //TODO: Do we need a `def` here?
  def diffContent: NonEmptySeq[String] = diffContentInternal
}

final class Predicate[A](val pair: (Defer[A], Defer[A]), val equalityType: EqualityType)(implicit val E: Equality[A], val D: Difference[A]) {


  /**
   * Add a name to a given predicate and create an [[boon.model.AssertionData]]
   * @param name The name associated with this predicate
   */
  def |(name: => String)(implicit loc: SourceLocation): AssertionData = new ContextAware[A](this, name).toAssertionData

  /**
   * Add a name to a given predicate and customise how to create an [[boon.model.AssertionData]]
   * @param name The name associated with this predicate
   */

  def ||(name: => String): ContextAware[A] = new ContextAware[A](this, name)

  //TODO: Change this to use a parameter class
  def >>(param: DiffContentParameter[A]): Predicate[A] =
    >** { case (equality, difference) =>
            (
              equality
              , param.mod match {
                  case Replace => Difference.fromResult[A](param.diffContent)
                  case Append  => Difference.appendResult(difference, param.diffContent)
                }
           )
        }

  private def >**(modify: (Equality[A], Difference[A]) => (Equality[A], Difference[A])): Predicate[A] = {
    val (equality, difference) = modify(E, D)
    new Predicate[A](pair, equalityType)(equality, difference)
  }
}
