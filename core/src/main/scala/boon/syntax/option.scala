package boon
package syntax

import boon.model.StringRep
import boon.model.AssertionData

object option {

  implicit final class OptionSyntax[A](value: => A) {
    def some: Option[A] = Some[A](value)
  }

  def none[A]: Option[A] = None

  def isSome[A: StringRep](option: Option[A])(implicit loc: SourceLocation): AssertionData =
    option.isDefined >> (one("expected Some got: None"), Replace) | ("is Some", "value" -> option.strRep)

  def some_?[A: StringRep](option: Option[A])(f: A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    option.fold(invalid(s"expected Some but got None") | "expect Some")(f(_).context(Map("value" -> option.strRep)))

  def none_?[A: StringRep](option: Option[A])(f: => AssertionData)(implicit loc: SourceLocation): AssertionData =
    option.fold(f)(_ => invalid(s"expected None but got: $option") | ("expect None", "value" -> option.strRep))

  def isNone[A: StringRep](option: Option[A])(implicit loc: SourceLocation): AssertionData =
    option.isEmpty >> (one(s"expected None got: ${option.strRep}"), Replace) | ("is None", "value" -> option.strRep)

}

