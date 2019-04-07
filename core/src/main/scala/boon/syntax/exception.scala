package boon.syntax

import boon.model.AssertionData
import boon.model.StringRep

import scala.util.Try
import scala.reflect.ClassTag

object exception {

  final class ExceptionSyntax[A](value: => A) {
    def =!=[T <: Throwable](assertMessage: String => AssertionData)(
      implicit classTag: ClassTag[T], SR: StringRep[A]): AssertionData = {
      val expectedClass = classTag.runtimeClass
      val expectedClassName = expectedClass.getName
      Try(value).fold[AssertionData](
        e => expectedClass.isAssignableFrom(e.getClass) |# (s"exception class ${expectedClassName}",
                                                             "expected class" -> expectedClassName,
                                                             "got class"      -> e.getClass.getName) and
             assertMessage(e.getMessage),
        s => fail(s"expected ${expectedClassName} but got class:${s.getClass.getName} value:${SR.strRep(s)}") | s"exception class ${expectedClassName}"
      )
    }
  }

  implicit def toExceptionSyntax[A](value: => A): ExceptionSyntax[A] = new ExceptionSyntax[A](value)
}