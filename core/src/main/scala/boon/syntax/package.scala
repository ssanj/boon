package boon

import boon.model._

import scala.util.Try

package object syntax {

  //implicits
  implicit def aToEqSyntax[A](value1: => A): EqSyntax[A] = new EqSyntax[A](value1)

  implicit def deferAToEqSyntax[A](dValue: Defer[A]): EqSyntax[A] =
    new EqSyntax[A](dValue.run) //this is safe because EqSyntax is lazy

  implicit def toAssertionDataFromSeqOfAssertionData(AssertionDataes: NonEmptySeq[AssertionData]): AssertionData =
    AssertionDataes.tail.foldLeft(AssertionDataes.head)(_ and _)

  implicit def toTestDataFromSeqOfAssertionData(AssertionDataes: NonEmptySeq[AssertionData]): TestData =
    toTestData(toAssertionDataFromSeqOfAssertionData(AssertionDataes))

  implicit def toTestData(AssertionData: AssertionData): TestData =
    TestData(AssertionData.assertions, Independent)

  implicit def booleanToPredicate(value1: => Boolean): Predicate[Boolean] =
    new Predicate[Boolean]((defer(value1), defer(true)), IsEqual, noHints)

  implicit def deferBooleanToPredicate(value: Defer[Boolean]): Predicate[Boolean] =
    new Predicate[Boolean]((value, defer(true)), IsEqual, noHints)

  //TODO: Do we need this?
  implicit def toStrRep[T: StringRep](value: T): StringRepSyntax[T] = StringRepSyntax[T](value)

  def fail(reason: String): Predicate[Boolean] = !true >> one(s"explicit fail: $reason")

  def pass: Predicate[Boolean] = true

  def %@[A](provide: => A, prefix: String*)(cs: A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    assertionBlock(cs(provide), prefix:_*)(loc)

  private def assertionBlock(cs: => AssertionData, prefix: String*)(implicit loc: SourceLocation): AssertionData = {
    val nameOp = for {
      fn  <- loc.fileName
    } yield s"assertion @ (${fn}:${loc.line})"

    val path = if (prefix.isEmpty) "" else prefix.mkString("",".", ".")
    val name = nameOp.fold(s"assertion @ ${path}(-:${loc.line})")(identity _)
    Try(cs).fold(ex => {
      defer[Boolean](throw ex) | s"${name} !!threw an Exception!!" //safe because it is deferred
    }, { ad =>

      AssertionData(ad.assertions.map(assertion => assertion.copy(name = AssertionName(s"${path}${assertion.name.value}"))))
    })
  }
}