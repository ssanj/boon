package boon

trait Testable {
  type Actual
  val value1: Actual
  val value2: Actual
  val equality: Equality[Actual]
  val difference: Difference[Actual]
}
