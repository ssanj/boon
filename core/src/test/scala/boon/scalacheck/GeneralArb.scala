package boon
package scalacheck

import model.Null
import model.Plain
import org.scalacheck._
import Arbitrary.arbitrary

object GeneralArb {

  implicit val throwableArbitrary: Arbitrary[Throwable] = Arbitrary {
    for {
      error <- arbitrary[String]
    } yield new RuntimeException(error)
  }

  implicit val nullArbitrary: Arbitrary[Null.type] = Arbitrary {
    Gen.const(Null)
  }

  implicit val plainArbitrary: Arbitrary[Plain] = Arbitrary {
    arbitrary[String].map(Plain(_))
  }
}
