package boon

trait Testable {
  type Actual
  val value1: Defer[Actual]
  val value2: Defer[Actual]
  val equality: Equality[Actual]
  val difference: Difference[Actual]
  val equalityType: EqualityType
}
