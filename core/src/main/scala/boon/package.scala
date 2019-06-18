package object boon {

import boon.model._
import boon.data.NonEmptySeq

import scala.util.Try

  def test(name: => String)(data: => TestData)(implicit testLocation: SourceLocation): Test =
    Try(data).fold(ex => {
      UnsuccessfulTest(ThrownTest(TestName(name), ex, testLocation))
    } , td => {
      SuccessfulTest(DeferredTest(TestName(name), td.assertions, td.combinator))
    })

  def table[T: StringRep, U: Equality : Difference: StringRep](name: => String, values: NonEmptyMap[T, (U, SourceLocation)])(f: T => U): Test = {
    SuccessfulTest(
      DeferredTest(
        TestName(name),
        //TODO: we may need to Try over these values
        values.map {
          case (t, (u, loc)) =>
            implicit val sl: SourceLocation = loc
            Boon.defineAssertion[U](s"with ${StringRep[T].strRep(t)} is ${StringRep[U].strRep(u)}", (Defer(() => f(t)), Defer(() => u)), IsEqual, noContext)
        },
        Independent
      )
    )
  }


  def xtest(name: => String)(data: => TestData): Test = {
    IgnoredTest(TestName(name), defer(data))
  }

  def defer[A](value: => A): Defer[A] = Defer[A](() => value)

  def tval[U](value: U)(implicit SL: SourceLocation): (U, SourceLocation) = (value, SL)

  type NonEmptyMap[K, V] = NonEmptySeq[(K, V)]

  def truthTable[K, V](head: (K, V), tail: (K, V)*): NonEmptyMap[K, V] =
    NonEmptySeq.nes[(K, V)](head, tail:_*)

  def noContext: Map[String, String] = Map.empty[String, String]

  def noErrorOverrides: Option[NonEmptySeq[String]] = None

  def oneOrMore[A](head: A, tail: A*): NonEmptySeq[A] = NonEmptySeq[A](head, tail.toSeq)

  def one[A](head: A): NonEmptySeq[A] = NonEmptySeq.one[A](head)

  val Replace = DiffReplace
  val Append  = DiffAppend

  type PredicateSyntax = syntax.PredicateSyntax

  //implicits
  implicit def aToEqSyntax[A](value1: => A): EqSyntax[A] = new EqSyntax[A](value1)

  implicit def deferAToEqSyntax[A](dValue: Defer[A]): EqSyntax[A] =
    new EqSyntax[A](dValue.run) //this is safe because EqSyntax is lazy

  implicit def toAssertionDataFromSeqOfAssertionData(assertionDatas: NonEmptySeq[AssertionData]): AssertionData =
    assertionDatas.tail.foldLeft(assertionDatas.head)(_ and _)

  implicit def toTestDataFromSeqOfAssertionData(assertionDatas: NonEmptySeq[AssertionData]): TestData =
    toTestData(toAssertionDataFromSeqOfAssertionData(assertionDatas))

  implicit def toTestData(AssertionData: AssertionData): TestData =
    TestData(AssertionData.assertions, Independent)

  implicit def booleanToPredicate(value1: => Boolean): Predicate[Boolean] =
    new Predicate[Boolean]((defer(value1), defer(true)), IsEqual)

  implicit def deferBooleanToPredicate(value: Defer[Boolean]): Predicate[Boolean] =
    new Predicate[Boolean]((value, defer(true)), IsEqual)

  implicit class StringRepSyntax[A](value: => A) {
    def strRep(implicit strRepA: StringRep[A]): String = strRepA.strRep(value)
  }

  def fail(reason: String): PredicateSyntax = new PredicateSyntax {

    override def |(name: => String, ctx: (String, String)*)(implicit loc: SourceLocation): AssertionData =
      false >> (one(s"explicit fail: $reason"), Replace) | (name, ctx:_*)
  }

  def pass: Predicate[Boolean] = true

  def %@[A](provide: => A)(cs: A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    assertionBlock(cs(provide), None)(loc)

  def %@[A](provide: => A, prefix: String)(cs: A => AssertionData)(implicit loc: SourceLocation): AssertionData =
    assertionBlock(cs(provide), Option(prefix))(loc)

  def ctx(first: (String, String), pairs: (String, String)*): NonEmptySeq[String] =
    oneOrMore(first, pairs:_*).map { case (k, v) =>  s"${k} -> ${v}"}

  private def assertionBlock(cs: => AssertionData, prefixOp: Option[String])(implicit loc: SourceLocation): AssertionData = {
    val nameOp = for {
      fn  <- loc.fileName
    } yield s"assertion @ (${fn}:${loc.line})"

    val namePath = prefixOp.fold("")(_ + " ")
    val name = nameOp.fold(s"assertion @ ${namePath}(-:${loc.line})")(identity _)
    Try(cs).fold[AssertionData](ex => {
      defer[Boolean](throw ex) | s"${name} !!threw an Exception!!" //safe because it is deferred
    }, { ad =>
        val path  = prefixOp.fold("")(p => s"${p}.")
        val assertionWithPath = ad.assertions.map(assertion => assertion.copy(name = AssertionName(s"${path}${assertion.name.value}")))
        AssertionData(assertionWithPath)
    })
  }
}