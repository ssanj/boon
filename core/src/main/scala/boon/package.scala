package object boon {

import boon.model._
import syntax.toStrRep
// import syntax.fail

// import scala.util.Try

  def test(name: => String)(data: TestData): DeferredTest = DeferredTest(TestName(name), data.assertions, data.combinator)

  // def test2(name: => String)(assertions: => NonEmptySeq[Assertion]): DeferredTest =
  //   Try(assertions).fold(ex => {
  //     DeferredTest(TestName(name), fail(ex.getMessage) | "Test threw an Exception")
  //   } , DeferredTest(TestName(name), _))

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