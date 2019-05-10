package boon
package scalacheck

import org.scalacheck._
import Arbitrary.arbitrary

object GeneralArb {

  implicit val throwableArbitrary: Arbitrary[Throwable] = Arbitrary {
    for {
      error <- arbitrary[String]
    } yield new RuntimeException(error)
  }
}
