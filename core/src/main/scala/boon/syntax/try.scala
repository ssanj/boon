package boon
package syntax

import boon.model.StringRep
import boon.model.AssertionData
import scala.util.Try

object `try` {

  def failure_?[A: StringRep](tryVal: Try[A])(f: Throwable => AssertionData)(implicit loc: SourceLocation): AssertionData = {
    tryVal.fold(f(_).context(inputM(tryVal)), _ => invalid(errorTemplate(plain("Failure"), tryVal)) || "expected Failure" |> one(input(tryVal)))
  }

  def isFailure[A: StringRep](tryVal: Try[A])(implicit loc: SourceLocation): AssertionData = {
    tryVal.fold(_ => pass || "is Failure" |> one(input(tryVal)),
                _ => invalid(errorTemplate(plain("Failure"), tryVal)) || "is Failure" |> one(input(tryVal)))
  }

  def success_?[A: StringRep](tryVal: Try[A])(f: A => AssertionData)(implicit loc: SourceLocation): AssertionData = {
    tryVal.fold(_ => invalid(errorTemplate(plain("Success"), tryVal)) || "expected Success" |> one(input(tryVal)), f(_).ctx(input(tryVal)))
  }

  def isSuccess[A: StringRep](tryVal: Try[A])(implicit loc: SourceLocation): AssertionData =
    tryVal.fold(_ => invalid(errorTemplate(plain("Success"), tryVal)) || "is Success" |> one(input(tryVal)),
                _ => pass || "is Success" |> one(input(tryVal)))
}

