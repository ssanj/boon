package boon
package syntax

object nulls {
  def null_![A](value: => A): DescSyntax[Boolean] = value != null

  def null_?[A](value: => A): DescSyntax[Boolean] = value == null
}
