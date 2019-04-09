package boon
package model

//All implicits for internal boon models.
object BoonTypes {

  implicit val assertionCombinatorBoonType = BoonType.defaults[AssertionCombinator]

  implicit val assertionBoonType = BoonType.defaults[Assertion]

  implicit val assertionDataBoonType = BoonType.defaults[AssertionData]
}

