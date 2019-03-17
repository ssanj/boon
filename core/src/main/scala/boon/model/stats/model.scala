package boon.model.stats

final case class StatusCount(passed: Int, failed: Int)

final case class AssertionCount(statusCount: StatusCount, notRun: Int)

final case class SuiteStats(suites: StatusCount, tests: StatusCount, assertions: AssertionCount)
