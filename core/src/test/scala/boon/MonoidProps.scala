package boon

import org.scalacheck.Properties
import org.scalacheck._
import boon.scalacheck.Arb._

import boon.model.stats.AssertionCount
import boon.model.stats.SuiteStats
import boon.model.stats.StatusCount

object MonoidProps extends Properties("Monoid") {

  monoidLaws[StatusCount]
  monoidLaws[AssertionCount]
  monoidLaws[SuiteStats]

  private def monoidLaws[A: Monoid: Arbitrary]: Unit = {
    property("left.identity") = Prop.forAll(leftIdentityLaw[A] _)

    property("right.identity") = Prop.forAll(rightIdentityLaw[A] _)

    property("associativity") = Prop.forAll(associativityLaw[A] _)
  }

  private def leftIdentityLaw[A: Monoid: Arbitrary](value: A): Boolean = {
    val monoidA = Monoid[A]

    monoidA.mappend(monoidA.mempty, value) == value
  }

  private def rightIdentityLaw[A: Monoid: Arbitrary](value: A): Boolean = {
    val monoidA = Monoid[A]

    monoidA.mappend(value, monoidA.mempty) == value
  }

  private def associativityLaw[A: Monoid](value1: A, value2: A, value3: A): Boolean = {
    val monoidA = Monoid[A]

    monoidA.mappend(monoidA.mappend(value1, value2), value3) ==
      monoidA.mappend(value1, monoidA.mappend(value2, value3))
  }
}