package boon
package syntax

object nulls {
  def null_![A](value: => A)(f : A => AssertionData): AssertionData =
    fold[A, AssertionData](value)(fail(s"expected not null value") | "not null value")(f)

  def null_?[A](value: => A)(f : => AssertionData): AssertionData =
    fold[A, AssertionData](value)(f)(v => fail(s"expected null but got: $v") | "null value")

  private def fold[A, B](value: => A)(n: => B)(s: A => B): B = Option(value).fold(n)(s)
}