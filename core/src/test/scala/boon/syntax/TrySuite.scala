package boon.syntax

import boon._
import syntax.`try`._
import syntax.exception._
import scala.util.Try

object TrySuite extends SuiteLike("Try Suite") {

  private val t1 = test("success") {
    val tryVal1 = Try("hello")
    val tryVal2 = Try(10)

    success_?(tryVal1)(_ =?= "hello" | "String success") and
    success_?(tryVal2)(_ =?= 10      | "Int Success")    and 
    isSuccess(tryVal1)                                   and
    isSuccess(tryVal2)
  }

  private val t2 = test("failures") {
    val tryVal1: Try[String] = Try(throw new RuntimeException("Ooops!"))

    failure_?(tryVal1)(_ =!=[RuntimeException](_ =?= "Ooops!" | "ex message")) and 
    isFailure(tryVal1)
  }

  override val tests = oneOrMore(t1, t2)
}