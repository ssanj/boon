package boon
package model

import org.scalacheck.Properties
import org.scalacheck.Arbitrary
import scala.reflect.runtime.universe._

/** Given a [[boon.BoonType]] and an org.scalacheck.Arbitrary instance, verifies 
  * laws for a valid [[boon.BoonType]] instance.
  *
  *  @example {{{
  * 
  *    object SomeTypeProps extends Properties("SomeType BoonType Laws") with BoonTypeLaws {
  *      //import SomeType's implicit BoonType instance into scope
  *      private implicit val arbSomeType: Arbitrary[SomeType] = ...
  *      include(checkLaws[SomeType])
  *    }
  *  }}}
  * 
  *  @tparam T Type to run [[boon.BoonType]] law checks against.
  *  @see [[boon.Equality]]
  *  @see [[boon.StringRep]]
  */
trait BoonTypeLaws {

  def checkLaws[T: BoonType : Arbitrary](implicit TT: TypeTag[T]): Properties =  {
    new Properties("checkLawsFor") with EqualityLawDefinition with StringRepLawDefinition {
      equalityLaws[T]
      strRepLaws[T]
    }
  }
}