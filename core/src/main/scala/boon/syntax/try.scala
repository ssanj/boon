package boon
package syntax

import boon.model.StringRep
import boon.model.AssertionData
import scala.util.Try

object `try` {

  def failure_?[A](tryVal: Try[A])(f: Throwable => AssertionData): AssertionData = {
    tryVal.fold(f, _ => fail(s"expected Failure but got ${tryVal}") | "expected Try#Failure" )
  }

  def isFailure[A: StringRep](tryVal: Try[A])(implicit loc: SourceLocation): AssertionData = {
    tryVal.fold(_ => pass | "is Try#Failure",
                _ => invalid(s"expected Failure got: ${StringRep[Try[A]].strRep(tryVal)}") | "is Try#Failure")
  }

  def success_?[A](tryVal: Try[A])(f: A => AssertionData): AssertionData = {
    tryVal.fold(_ => fail(s"expected Success but got ${tryVal}") | "expected Try#Success", f)
  }

  def isSuccess[A: StringRep](tryVal: Try[A])(implicit loc: SourceLocation): AssertionData =
    tryVal.fold(_ => invalid(s"expected Success got: ${StringRep[Try[A]].strRep(tryVal)}") | "is Try#Success",
                _ => pass | "is Try#Success")
}

