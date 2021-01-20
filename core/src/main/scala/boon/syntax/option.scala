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
    option.isDefined.>> (one(errorTemplate(plain("Some"), plain("None"))))(Replace) || "is Some" |> one(input(option))

  def some_?[A: StringRep](option: Option[A])(f: A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    option.fold[AssertionData](invalid(errorTemplate(plain("Some"), plain("None"))) || "expect Some")(f(_).context(inputM(option)))

  def none_?[A: StringRep](option: Option[A])(f: => AssertionData)(implicit loc: SourceLocation): AssertionData =
    option.fold(f)(_ => invalid(errorTemplate(plain("None"), option)) || "expect None" |> one(input(option)))

  def isNone[A: StringRep](option: Option[A])(implicit loc: SourceLocation): AssertionData =
    option.isEmpty.>> (one(errorTemplate(plain("None"), option)))(Replace) || "is None" |> one(input(option))

}

