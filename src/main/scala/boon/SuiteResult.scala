package boon

//This could be a Monoid
// AllPassed  + AllPassed => AllPassed
// AllPassed  + SomePassed => SomePassed
// AllPassed  + AllFailed => SomePassed
// AllPassed  + NoTests => AllPassed
// SomePassed  + AllPassed => SomePassed
// SomePassed  + AllFailed => SomePassed
// SomePassed  + NoTests => SomePassed
// AllFailed  + AllPassed => SomePassed
// AllFailed  + SomePassed => SomePassed
// AllFailed  + AllFailed => AllFailed
// AllFailed  + NoTests => AllFailed
// NoTests  + AllPassed => AllPassed
// NoTests  + SomePassed => SomePassed
// NoTests  + AllFailed => AllFailed
// NoTests  + NoTests => NoTests
sealed trait SuiteResult
//Should these be NonEmptyLists?
final case class AllPassed(name: String, passed: Seq[TestResult.Success]) extends SuiteResult
final case class SomePassed(name: String, passed: Seq[TestResult.Success], failed: Seq[TestResult.Failure]) extends SuiteResult
final case class AllFailed(name: String, failed: Seq[TestResult.Failure]) extends SuiteResult
//Remove this by using a NEL on construction of the Suite
final case class NoTests(name: String) extends SuiteResult
