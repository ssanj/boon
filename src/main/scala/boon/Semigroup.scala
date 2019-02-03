package boon

trait Semigroup[A] {
  def mappend(value1: A, value2: A): A
}