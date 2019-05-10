package boon
package scalacheck

import boon.data.NonEmptySeq

import org.scalacheck._
import Arbitrary.arbitrary
import scala.util.Random.{shuffle => rShuffle}

object DataArb {

  implicit def nonEmptySeqArb[A: Arbitrary]: Arbitrary[NonEmptySeq[A]] = Arbitrary {
    for {
      value1 <- arbitrary[A]
      length <- Gen.choose(0, 4)
      values <- Gen.listOfN(length, arbitrary[A])
    } yield oneOrMore(value1, values:_*)
  }

  //TODO: Move to common functions class
  def shuffle[A](values: NonEmptySeq[A]): NonEmptySeq[A] = {
    val shuffled = rShuffle(rShuffle(values.toVector))
    oneOrMore(shuffled.head, shuffled.tail:_*)
  }
}
