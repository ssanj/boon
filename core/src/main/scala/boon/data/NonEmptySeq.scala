package boon
package data

//Minimal implementation of NonEmptySeq
//Do we need to use Vector instead for performance?
//Generalise? NonEmpty[F[_], A]
// - F needs: map, flatMap, fold, prepend, append, reverse
final case class NonEmptySeq[A](head: A, tail: Seq[A]) { self =>

  def map[B](f: A => B): NonEmptySeq[B] = NonEmptySeq(f(head), tail.map(f))

  def flatMap[B](f: A => NonEmptySeq[B]): NonEmptySeq[B] = {
    tail.map(f).foldLeft(f(head))((acc, v) => acc.concat(v))
  }

  def filter(f: A => Boolean): Seq[A] =
    if (f(head)) head +: tail.filter(f) else tail.filter(f)

  def contains(value: A): Boolean = filter(_ == value).nonEmpty

  def exists(p: A => Boolean): Boolean = filter(p).nonEmpty

  def mkString(sep: String): String = toSeq.mkString(sep)

  def mkString(start: String, sep: String, end: String): String = toSeq.mkString(start, sep, end)

  def concat(other: NonEmptySeq[A]): NonEmptySeq[A] = self.copy(tail = self.tail ++ other.toSeq)

  def ++(other: Seq[A]): NonEmptySeq[A] = self.copy(tail = self.tail ++ other)

  def reverse: NonEmptySeq[A] = {
    val reversed = toSeq.reverse //we know this is not empty
    NonEmptySeq(reversed.head, reversed.tail)
  }

  //Adapted from Cats NonEmptyList
  //https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/NonEmptyList.scala
  def zipWithIndex: NonEmptySeq[(A, Int)] = {
    val bldr = Vector.newBuilder[(A, Int)]
    var idx = 1
    val it = tail.iterator
    while (it.hasNext) {
      bldr += ((it.next(), idx))
      idx += 1
    }

    NonEmptySeq((head, 0), bldr.result())
  }

  def zip[B](other: NonEmptySeq[B]): NonEmptySeq[(A, B)] = {
    val headO = other.head
    val tailO = other.tail

    NonEmptySeq((head, headO) ,tail.zip(tailO))
  }

  def zipAll[B](other: NonEmptySeq[B], defA: => A, defB: => B): NonEmptySeq[(A, B)] = {
    val headO = other.head
    val tailO = other.tail

    NonEmptySeq((head, headO) ,tail.zipAll(tailO, defA, defB))
  }

  def foreach(f: A => Unit): Unit = {
   val _ = map(f)
   ()
  }

  def toSeq: Seq[A] = head +: tail

  def toList: List[A] = head +: tail.toList

  def toVector: Vector[A] = head +: tail.toVector

  def length: Int = tail.length + 1

  def prepend(newHead: A): NonEmptySeq[A] = NonEmptySeq[A](newHead, self.head +: tail)

  def +:(newHead: A): NonEmptySeq[A] = prepend(newHead)

  def append(last: A): NonEmptySeq[A] = NonEmptySeq[A](head, tail :+ last)

  def foldLeft[B](z: B)(f:(B, A) => B): B =
    tail.foldLeft(f(z, head))(f)

  def partition[B, C](f: A => Either[B, C]): These[NonEmptySeq[B], NonEmptySeq[C]] = {
    val eitherSeq: NonEmptySeq[Either[B, C]] = self.map(f).reverse

    val headResult: These[NonEmptySeq[B], NonEmptySeq[C]] = eitherSeq.head match {
      case Left(value) => These.OnlyLeft(one(value))
      case Right(value) => These.OnlyRight(one(value))
    }

    eitherSeq.tail.foldLeft(headResult) {
      case (These.OnlyLeft(lacc), Left(l))    => These.OnlyLeft(lacc.prepend(l))
      case (These.OnlyLeft(lacc), Right(r))   => These.Both(lacc, one(r))
      case (These.OnlyRight(racc), Left(l))   => These.Both(one(l), racc)
      case (These.OnlyRight(racc), Right(r))  => These.OnlyRight(racc.prepend(r))
      case (These.Both(lacc, racc), Left(l))  => These.Both(lacc.prepend(l), racc)
      case (These.Both(lacc, racc), Right(r)) => These.Both(lacc, racc.prepend(r))
    }
  }

  def find(f: A => Boolean): Option[A] = {
    if (f(head)) Some(head)
    else tail.find(f)
  }

  def last: A = if (NonEmptySeq.headOnly[A](this)) head else tail.last

  def get(index: Int): Option[A] =
    if (index >= 0 && index < length) {
      if (index == 0) Some(head) else tail.drop(index - 1).headOption
    } else None
}

object NonEmptySeq {

  def nes[A](head: A, tail: A*): NonEmptySeq[A] = NonEmptySeq[A](head, tail.toSeq)

  def one[A](head: A): NonEmptySeq[A] = NonEmptySeq.nes[A](head)

  def fromVector[A](value: Vector[A]): Option[NonEmptySeq[A]] =
    value match {
      case h +: t => Some(NonEmptySeq(h, t))
      case _      => None
    }

  def headOnly[A](xs: NonEmptySeq[A]): Boolean = xs.tail.isEmpty

  def nonEmptyTail[A](xs: NonEmptySeq[A]): Boolean = xs.tail.nonEmpty
}