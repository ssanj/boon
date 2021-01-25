package boon
package model

import boon.data.NonEmptySeq
import Boon.defineAssertion

final class ContextAware[A](val pred: Predicate[A], name: => String) {
  def |>(ctx: NonEmptySeq[(String, String)])(implicit loc: SourceLocation): AssertionData =
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, (pred.pair), pred.equalityType, Map(ctx.toSeq:_*))(pred.E, pred.D, loc)
      )
    )

  def toAssertionData(implicit loc: SourceLocation): AssertionData =
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, (pred.pair), pred.equalityType, Map.empty)(pred.E, pred.D, loc)
      )
    )

}


final class Predicate[A](val pair: (Defer[A], Defer[A]), val equalityType: EqualityType)(implicit val E: Equality[A], val D: Difference[A]) {


  /**
   * Add a name to a given predicate
   * @param name The name associated with this predicate
   */
  def |(name: => String): ContextAware[A] = new ContextAware[A](this, name)


  def |?(name: => String, difference: Difference[A], equality: Equality[A], ctx: Map[String, String])(implicit loc: SourceLocation): AssertionData = {
    new AssertionData(
      NonEmptySeq.nes(
        defineAssertion[A](name, (pair), equalityType, ctx)(equality, difference, loc)))
  }

  def >>(diffContent: => NonEmptySeq[String])(mod: DifferenceMod): Predicate[A] =
    >** { case (equality, difference) =>
            (
              equality
              , mod match {
                  case Replace => Difference.fromResult[A](diffContent)
                  case Append  => Difference.appendResult(difference, diffContent)
                }
           )
        }

  private def >**(modify: (Equality[A], Difference[A]) => (Equality[A], Difference[A])): Predicate[A] = {
    val (equality, difference) = modify(E, D)
    new Predicate[A](pair, equalityType)(equality, difference)
  }
}
