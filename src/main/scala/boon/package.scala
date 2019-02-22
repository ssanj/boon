package object boon {
  def test(name: => String)(assertions: NonEmptySeq[Defer[Assertion]]): DeferredTest = DeferredTest(TestName(name), assertions)
}