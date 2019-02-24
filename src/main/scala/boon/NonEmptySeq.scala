package boon

//Minimal implementation of NonEmptySeq
//Do we need to use Vector instead for performance?
//Generalise? NonEmpty[F[_], A]
// - F needs: map, flatMap, fold, prepend, append, reverse
final case class NonEmptySeq[A](head: A, tail: Seq[A]) { self =>

  def map[B](f: A => B): NonEmptySeq[B] = NonEmptySeq(f(head), tail.map(f))

  def flatMap[B](f: A => NonEmptySeq[B]): NonEmptySeq[B] = {
    tail.map(f).foldLeft(f(head))((acc, v) => acc.concat(v))
  }

  def concat(other: NonEmptySeq[A]): NonEmptySeq[A] = self.copy(tail = self.tail ++ other.toSeq)

  def reverse: NonEmptySeq[A] = {
    val reversed = toSeq.reverse //we know this is not empty
    NonEmptySeq(reversed.head, reversed.tail)
  }

  def foreach(f: A => Unit): Unit = map(f)

  def toSeq: Seq[A] = head +: tail

  def prepend(newHead: A): NonEmptySeq[A] = NonEmptySeq[A](newHead, self.head +: tail)

  def append(last: A): NonEmptySeq[A] = NonEmptySeq[A](head, tail :+ last)

  def partition[B, C](f: A => Either[B, C]): These[NonEmptySeq[B], NonEmptySeq[C]] = {
    val eitherSeq: NonEmptySeq[Either[B, C]] = self.map(f).reverse

    val headResult: These[NonEmptySeq[B], NonEmptySeq[C]] = eitherSeq.head match {
      case Left(value) => These.OnlyLeft(NonEmptySeq.one(value))
      case Right(value) => These.OnlyRight(NonEmptySeq.one(value))
    }

    eitherSeq.tail.foldLeft(headResult) {
      case (These.OnlyLeft(lacc), Left(l))    => These.OnlyLeft(lacc.prepend(l))
      case (These.OnlyLeft(lacc), Right(r))   => These.Both(lacc, NonEmptySeq.one(r))
      case (These.OnlyRight(racc), Left(l))   => These.Both(NonEmptySeq.one(l), racc)
      case (These.OnlyRight(racc), Right(r))  => These.OnlyRight(racc.prepend(r))
      case (These.Both(lacc, racc), Left(l))  => These.Both(lacc.prepend(l), racc)
      case (These.Both(lacc, racc), Right(r)) => These.Both(lacc, racc.prepend(r))
    }
  }

  def find(f: A => Boolean): Option[A] = {
    if (f(head)) Some(head)
    else tail.find(f)
  }
}

object NonEmptySeq {

  def nes[A](head: A, tail: A*): NonEmptySeq[A] = NonEmptySeq[A](head, tail.toSeq)

  def one[A](head: A): NonEmptySeq[A] = nes[A](head)

  def isHeadOnly[A](xs: NonEmptySeq[A]): Boolean = xs.tail.isEmpty
}