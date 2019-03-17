package boon.model.stats

import boon.Monoid

final case class StatusCount(passed: Int, failed: Int)

object StatusCount {

  implicit val statusCountMonoid: Monoid[StatusCount] = new Monoid[StatusCount] {

    override def mempty: StatusCount = StatusCount(0, 0)

    override def mappend(x: StatusCount, y: StatusCount): StatusCount = (x, y) match {
      case (StatusCount(passed1, failed1), StatusCount(passed2, failed2)) => StatusCount(passed1 + passed2, failed1 + failed2)
    }
  }
}

final case class AssertionCount(statusCount: StatusCount, notRun: Int)

object AssertionCount {

  implicit val assertionCountMonoid: Monoid[AssertionCount] = new Monoid[AssertionCount] {

    private val monoidStatusCount = Monoid[StatusCount]

    override def mempty: AssertionCount = AssertionCount(monoidStatusCount.mempty, 0)

    override def mappend(x: AssertionCount, y: AssertionCount): AssertionCount = (x, y) match {
      case (AssertionCount(StatusCount(passed1, failed1), notRun1), AssertionCount(StatusCount(passed2, failed2), notRun2)) =>
        AssertionCount(StatusCount(passed1 + passed2, failed1 + failed2), notRun1 + notRun2)
    }
  }
}

final case class SuiteStats(suites: StatusCount, tests: StatusCount, assertions: AssertionCount)

object SuiteStats {

  implicit val suiteStatusMonoid: Monoid[SuiteStats] = new Monoid[SuiteStats] {

    private val monoidStatusCount = Monoid[StatusCount]
    private val monoidAssertionCount = Monoid[AssertionCount]

    override def mempty: SuiteStats = SuiteStats(monoidStatusCount.mempty, monoidStatusCount.mempty, monoidAssertionCount.mempty)

    override def mappend(x: SuiteStats, y: SuiteStats): SuiteStats = (x, y) match {
      case (SuiteStats(suites1, tests1, assertions1), SuiteStats(suites2, tests2, assertions2)) =>
        SuiteStats(suites     = monoidStatusCount.mappend(suites1, suites2),
                   tests      = monoidStatusCount.mappend(tests1, tests2),
                   assertions = monoidAssertionCount.mappend(assertions1, assertions2)
       )
    }
  }
}
