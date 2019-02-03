package boon

//Minimal implementation of NonEmptySeq
//Do we need to use Vector instead for performance?
final case class NonEmptySeq[A](head: A, tail: Seq[A]) { self =>

  def map[B](f: A => B): NonEmptySeq[B] = NonEmptySeq(f(head), tail.map(f))

  def flatMap[B](f: A => NonEmptySeq[B]): NonEmptySeq[B] = {
    tail.map(f).foldLeft(f(head))((acc, v) => acc.concat(v))
  }

  def concat(other: NonEmptySeq[A]): NonEmptySeq[A] = self.copy(tail = self.tail ++ other.toSeq)

  def toSeq: Seq[A] = head +: tail

  //add partition method that returns a These - that way we don't handle the double empty case
}

object NonEmptySeq {
  def nes[A](head: A, tail: A*): NonEmptySeq[A] = NonEmptySeq[A](head, tail.toSeq)
}