package boon
package syntax

import boon.model.StringRep
import boon.model.AssertionData
import scala.util.Try

object `try` {

  def failure_?[A: StringRep](tryVal: Try[A])(f: Throwable => AssertionData)(implicit loc: SourceLocation): AssertionData = {
    tryVal.fold(f(_).context(inputM(tryVal)), _ => invalid(errorTemplate(plain("Failure"), tryVal)) | ("expected Failure", input(tryVal)))
  }

  def isFailure[A: StringRep](tryVal: Try[A])(implicit loc: SourceLocation): AssertionData = {
    tryVal.fold(_ => pass | "is Try#Failure",
                _ => invalid(s"expected Failure got: ${StringRep[Try[A]].strRep(tryVal)}") | "is Try#Failure")
  }

  def success_?[A: StringRep](tryVal: Try[A])(f: A => AssertionData)(implicit loc: SourceLocation): AssertionData = {
    tryVal.fold(_ => invalid(errorTemplate(plain("Success"), tryVal)) | ("expected Success", input(tryVal)), f(_).context(inputM(tryVal)))
  }

  def isSuccess[A: StringRep](tryVal: Try[A])(implicit loc: SourceLocation): AssertionData =
    tryVal.fold(_ => invalid(s"expected Success got: ${StringRep[Try[A]].strRep(tryVal)}") | "is Try#Success",
                _ => pass | "is Try#Success")
}

