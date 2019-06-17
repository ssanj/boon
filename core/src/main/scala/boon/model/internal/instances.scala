package boon
package model
package internal

import boon.result.AssertionOutput

//All implicits for internal boon models.
object instances {
  implicit val assertionCombinatorBoonType = BoonType.defaults[AssertionCombinator]

  implicit val assertionBoonType = BoonType.defaults[Assertion]

  implicit val assertionDataBoonType = BoonType.defaults[AssertionData]
  
  implicit val assertionResultBoonType = BoonType.defaults[AssertionResult]
  
  implicit val assertionOutputBoonType = BoonType.defaults[AssertionOutput]

  implicit val assertionStateBoonType = BoonType.defaults[AssertionState]

  implicit val testStateBoonType = BoonType.defaults[TestState]

  implicit val suiteStateBoonType = BoonType.defaults[SuiteState]

  implicit val equalityTypeBoonType = BoonType.defaults[EqualityType]
}

