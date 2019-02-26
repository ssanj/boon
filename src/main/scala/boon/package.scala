package object boon {

import syntax.toStrRep

  def test(name: => String)(assertions: NonEmptySeq[Assertion]): DeferredTest = DeferredTest(TestName(name), assertions)

  def table[T: StringRep, U: Equality : Difference: StringRep](name: => String, values: NonEmptyMap[T, U])(f: T => U): DeferredTest = {
    DeferredTest(TestName(name), values.map {
      case (t, u) => Boon.defineAssertion[U](s"with ${t.strRep} is ${u.strRep}", (Defer(() => f(t)), Defer(() => u)))
    })
  }

  type NonEmptyMap[K, V] = NonEmptySeq[(K, V)]
}