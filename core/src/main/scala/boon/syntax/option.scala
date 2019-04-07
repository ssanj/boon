package boon.syntax

object option {

  final class OptionSyntax[A](value: => A) {
    def some: Option[A] = Some[A](value)
  }

  implicit def toOptionSyntax[A](value: => A): OptionSyntax[A] = new OptionSyntax[A](value)

  def none[A]: Option[A] = None

  def some_?[A](option: Option[A])(f: A => ContinueSyntax): ContinueSyntax =
    option.fold(fail(s"expected Some but got None") | "expect Some")(f)

  def none_?[A](option: Option[A])(f: => ContinueSyntax): ContinueSyntax =
    option.fold(f)(_ => fail(s"expected None but got: $option") | "expect None")
}

