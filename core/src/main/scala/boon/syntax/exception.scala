package boon
package syntax

import boon.model.Difference
import boon.model.AssertionData
import boon.model.StringRep
import boon.model.Equality.genEq

import scala.util.Try
import scala.reflect.ClassTag

object exception {

  private def assertException[T <: Throwable](e: => Throwable, assertMessage: String => AssertionData, loc: SourceLocation)(
    implicit classTag: ClassTag[T]): AssertionData = {
      val expectedClass = classTag.runtimeClass
      val expectedClassName = expectedClass.getName
      val diff = Difference.fromResult[Boolean](one(s"expected: $expectedClassName got: ${e.getClass.getName}"))

      //supply the location of the invocation here,
      //otherwise the error will point to the line below
      expectedClass.isAssignableFrom(e.getClass).|?("exception class", diff, genEq, noContext)(loc) and
      assertMessage(e.getMessage)
  }

  final class ExceptionSyntax[A](value: => A) {
    def =!=[T <: Throwable](assertMessage: String => AssertionData)(
      implicit classTag: ClassTag[T], SR: StringRep[A], loc: SourceLocation): AssertionData = {
      val expectedClass = classTag.runtimeClass
      val expectedClassName = expectedClass.getName
      Try(value).fold[AssertionData](e =>
        assertException[T](e, assertMessage, loc),
        s => fail(s"expected ${expectedClassName} but got class:${s.getClass.getName} value:${SR.strRep(s)}") | s"exception class ${expectedClassName}"
      )
    }
  }

  final class ThrowableSyntax(e: => Throwable) {
    def =!=[T <: Throwable](assertMessage: String => AssertionData)(
      implicit classTag: ClassTag[T], loc: SourceLocation): AssertionData = {
      assertException[T](e, assertMessage, loc)
    }
  }

  implicit def toExceptionSyntax[A](value: => A): ExceptionSyntax[A] = new ExceptionSyntax[A](value)

  implicit def toThrowableSyntax(value: => Throwable): ThrowableSyntax = new ThrowableSyntax(value)
}