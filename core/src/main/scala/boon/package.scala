package object boon {

import syntax.toStrRep

  def test(name: => String)(assertions: NonEmptySeq[Assertion]): DeferredTest = DeferredTest(TestName(name), assertions)

  def table[T: StringRep, U: Equality : Difference: StringRep](name: => String, values: NonEmptyMap[T, (U, SourceLocation)])(f: T => U): DeferredTest = {
    DeferredTest(TestName(name), values.map {
      case (t, (u, loc)) =>
        implicit val sl: SourceLocation = loc
        Boon.defineAssertion[U](s"with ${t.strRep} is ${u.strRep}", (Defer(() => f(t)), Defer(() => u)))
    })
  }

  def tval[U](value: U)(implicit SL: SourceLocation): (U, SourceLocation) = (value, SL)

  type NonEmptyMap[K, V] = NonEmptySeq[(K, V)]

  object FailedAssertion

  object PassedAssertion
}