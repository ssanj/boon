package boon
package model

sealed trait AssertionState

object AssertionState {
  case object Passed extends AssertionState
  case object Failed extends AssertionState

  implicit val assertionStateBoonType = BoonType.defaults[AssertionState]
}

sealed trait TestState

object TestState {
  case object Passed  extends TestState
  case object Failed  extends TestState
  case object Ignored  extends TestState

  implicit val assertionStateBoonType = BoonType.defaults[TestState]
}

sealed trait SuiteState

object SuiteState {
  case object Passed extends SuiteState
  case object Failed extends SuiteState

  implicit val assertionStateBoonType = BoonType.defaults[SuiteState]
}
