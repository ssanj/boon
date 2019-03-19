package object boon {

import boon.model._
import syntax.toStrRep
import syntax.frameworkFail

import scala.util.Try

  def test(name: => String)(data: => TestData)(implicit testLocation: SourceLocation): DeferredTest =
    Try(data).fold(ex => {
      // ex.printStackTrace
      DeferredTest(TestName(name), (frameworkFail(s"${ex.getMessage}") | "!!Test threw an Exception!!").assertions, Independent)
    } , td => {
      DeferredTest(TestName(name), td.assertions, td.combinator)
    })

  def table[T: StringRep, U: Equality : Difference: StringRep](name: => String, values: NonEmptyMap[T, (U, SourceLocation)])(f: T => U): DeferredTest = {
    DeferredTest(
      TestName(name),
      values.map {
        case (t, (u, loc)) =>
          implicit val sl: SourceLocation = loc
          Boon.defineAssertion[U](s"with ${t.strRep} is ${u.strRep}", (Defer(() => f(t)), Defer(() => u)), IsEqual)
      },
      Independent
    )
  }

  def defer[A](value: => A): Defer[A] = Defer[A](() => value)

  def tval[U](value: U)(implicit SL: SourceLocation): (U, SourceLocation) = (value, SL)

  type NonEmptyMap[K, V] = NonEmptySeq[(K, V)]

  def noContext: Map[String, String] = Map.empty[String, String]
}