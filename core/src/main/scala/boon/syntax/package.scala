package boon

import boon.model._

import scala.util.Try

package object syntax {

  //implicits
  implicit def aToEqSyntax[A](value1: => A): EqSyntax[A] = new EqSyntax[A](value1)

  implicit def deferAToEqSyntax[A](dValue: Defer[A]): EqSyntax[A] =
    new EqSyntax[A](dValue.run) //this is safe because EqSyntax is lazy

  implicit def toContinueSyntaxFromSeqOfContinueSyntax(continueSyntaxes: NonEmptySeq[ContinueSyntax]): ContinueSyntax =
    continueSyntaxes.tail.foldLeft(continueSyntaxes.head)(_ and _)

  implicit def toTestDataFromSeqOfContinueSyntax(continueSyntaxes: NonEmptySeq[ContinueSyntax]): TestData =
    toTestData(toContinueSyntaxFromSeqOfContinueSyntax(continueSyntaxes))

  implicit def toTestData(continueSyntax: ContinueSyntax): TestData =
    TestData(continueSyntax.assertions, Independent)

  implicit def booleanToDescSyntax(value1: => Boolean): DescSyntax[Boolean] =
    new DescSyntax[Boolean]((defer(value1), defer(true)), IsEqual, noHints)

  implicit def deferBooleanToDescSyntax(value: Defer[Boolean]): DescSyntax[Boolean] =
    new DescSyntax[Boolean]((value, defer(true)), IsEqual, noHints)

  //TODO: Do we need this?
  implicit def toStrRep[T: StringRep](value: T): StringRepSyntax[T] = StringRepSyntax[T](value)

  //descriptive
  def assertionBlock(cs: => ContinueSyntax)(implicit loc: SourceLocation): ContinueSyntax = {
    val nameOp = for {
      fn  <- loc.fileName
    } yield s"assertion @ (${fn}:${loc.line})"

    val name = nameOp.fold(s"assertion @ (-:${loc.line})")(identity _)
    Try(cs).fold(ex => {
      defer[Boolean](throw ex) | s"${name} !!threw an Exception!!"
    }, identity _)
  }

  def fail(reason: String): DescSyntax[Boolean] = !true >> Seq(s"explicit fail: $reason")

  def pass: DescSyntax[Boolean] = true

  def %@[A](provide: => A)(cs: A => ContinueSyntax)(implicit loc: SourceLocation): ContinueSyntax = assertionBlock(cs(provide))(loc)

}