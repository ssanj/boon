package boon
package syntax

object notNull {
  def null_![A](value: => A): DescSyntax[Boolean] = value != null

  def null_?[A](value: => A): DescSyntax[Boolean] = value == null
}
