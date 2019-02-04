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

  def partition[B, C](f: A => Either[B, C]): These[NonEmptySeq[B], NonEmptySeq[C]] = {
    val eitherSeq: NonEmptySeq[Either[B, C]] = self.map(f)

    val (lefts, rights) = boon.partitionWith[Either[B, C], NonEmptySeq.L[B], NonEmptySeq.R[C]](
      eitherSeq.toSeq,
      { case Left(value) => NonEmptySeq.L[B](value) },
      { case Right(value) => NonEmptySeq.R[C](value)}
    )

    (lefts, rights) match {
      case (Seq(), Seq()) => ??? //fix
      case (x +: xs, Seq()) => OnlyLeft(NonEmptySeq[B](x.value, xs.map(_.value)))
      case (Seq(), y +: ys) => OnlyRight(NonEmptySeq[C](y.value, ys.map(_.value)))
      case (x +: xs, y +: ys) => Both(NonEmptySeq[B](x.value, xs.map(_.value)), NonEmptySeq[C](y.value, ys.map(_.value)))
    }
  }
}

object NonEmptySeq {

  final case class R[A](value: A)
  final case class L[A](value: A)

  def nes[A](head: A, tail: A*): NonEmptySeq[A] = NonEmptySeq[A](head, tail.toSeq)
}