package boon
package syntax

import boon.model.StringRep
import boon.model.AssertionData

object option {

  final class OptionSyntax[A](value: => A) {
    def some: Option[A] = Some[A](value)
  }

  implicit def toOptionSyntax[A](value: => A): OptionSyntax[A] = new OptionSyntax[A](value)

  def none[A]: Option[A] = None

  def isSome[A](option: Option[A])(implicit loc: SourceLocation): AssertionData =
    option.isDefined >> (one("expected Some got: None"), Replace) | "is Some"

  def some_?[A](option: Option[A])(f: A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    option.fold(fail(s"expected Some but got None") | "expect Some")(f)

  def none_?[A](option: Option[A])(f: => AssertionData)(implicit loc: SourceLocation): AssertionData =
    option.fold(f)(_ => fail(s"expected None but got: $option") | "expect None")

  def isNone[A: StringRep](option: Option[A])(implicit loc: SourceLocation): AssertionData =
    option.isEmpty >> (one(s"expected None got: ${StringRep[Option[A]].strRep(option)}"), Replace) | "is None"

}

