package boon
package model

import org.scalacheck.Gen
import org.scalacheck.Properties
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object CustomBoonTypeProps extends Properties("Custom BoonType Laws") with BoonTypeLaws {

  final case class Person(name: String, age: Int)
  
  sealed trait Door
  case object Opened extends Door
  case object Closed extends Door

  private implicit val boonTypePerson = BoonType.defaults[Person]
  
  private implicit val boonTypeDoor = BoonType.defaults[Door]

  private implicit val arbPerson = Arbitrary(arbitrary[(String, Int)].map(Person.tupled))

  private implicit val arbDoor = Arbitrary(
    Gen.oneOf[Door](Opened, Closed)
  )

  include(checkLaws[Person])
  include(checkLaws[Door])
}