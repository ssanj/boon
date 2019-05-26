package boon
package syntax

import boon.model.AssertionData
import scala.util.Try

object `try` {

  def failure_?[A](tryVal: Try[A])(f: Throwable => AssertionData): AssertionData = {
    tryVal.fold(f, _ => fail(s"expected Failure but got ${tryVal}") | "expected Failure" )
  }

  def success_?[A](tryVal: Try[A])(f: A => AssertionData): AssertionData = {
    tryVal.fold(_ => fail(s"expected Success but got ${tryVal}") | "expected Success", f)
  }
}

