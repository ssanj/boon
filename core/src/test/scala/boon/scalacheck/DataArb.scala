package boon
package scalacheck

import boon.data.NonEmptySeq

import org.scalacheck._
import Arbitrary.arbitrary

object DataArb {

  implicit def nonEmptySeqArb[A: Arbitrary]: Arbitrary[NonEmptySeq[A]] = Arbitrary {
    for {
      value1 <- arbitrary[A]
      length <- Gen.choose(0, 4)
      values <- Gen.listOfN(length, arbitrary[A])
    } yield oneOrMore(value1, values:_*)
  }
}
