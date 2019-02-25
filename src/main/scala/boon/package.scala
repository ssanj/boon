package object boon {
  def test(name: => String)(assertions: NonEmptySeq[Assertion]): DeferredTest = DeferredTest(TestName(name), assertions)
}