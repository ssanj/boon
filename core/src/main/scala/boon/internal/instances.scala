package boon
package internal

import model._
import result._

//All implicits for internal boon types - mainly used for testing boon.
object instances {
  implicit val assertionCombinatorBoonType     = BoonType.defaults[AssertionCombinator]

  implicit val assertionBoonType               = BoonType.defaults[Assertion]

  implicit val assertionDataBoonType           = BoonType.defaults[AssertionData]
  
  implicit val assertionResultBoonType         = BoonType.defaults[AssertionResult]
  
  implicit val assertionOutputBoonType         = BoonType.defaults[AssertionOutput]

  implicit val assertionStateBoonType          = BoonType.defaults[AssertionState]

  implicit val testStateBoonType               = BoonType.defaults[TestState]

  implicit val sequentialPassBoonType          = BoonType.defaults[SequentialPass]
  
  implicit val sequentialNotRunBoonType        = BoonType.defaults[SequentialNotRun]

  implicit val suiteStateBoonType              = BoonType.defaults[SuiteState]

  implicit val equalityTypeBoonType            = BoonType.defaults[EqualityType]

  implicit val assertionFailureDoubleeBoonType = BoonType.defaults[AssertionFailureDouble]
}

