package example

import _root_.boon.NonEmptySeq
import _root_.boon.Equality
import _root_.boon.Difference
import _root_.boon.StringRep

class Stack[A](private var internal: NonEmptySeq[A]) {
  def push(value: A): Stack[A] = new Stack[A](internal.prepend(value))

  def pop(): (A, Option[Stack[A]]) = internal match {
    case NonEmptySeq(h, Seq()) => (h, None)
    case NonEmptySeq(h, x +: xs) => (h, Some(new Stack(NonEmptySeq(x, xs))))
  }
}

//Custom types need to define `Equality`, `StringRep` and `Difference`.
object Stack {
  implicit def stackEquality[A]: Equality[Stack[A]] = new Equality[Stack[A]] {
    override def eql(s1: Stack[A], s2: Stack[A]): Boolean = s1.internal == s2.internal
  }

  implicit def stackStringRep[A]: StringRep[Stack[A]] = new StringRep[Stack[A]] {
    override def strRep(s1: Stack[A]): String = s"Stack[${s1.internal.toSeq.mkString(",")}]"
  }

  implicit def stackDifference[A: StringRep]: Difference[Stack[A]] = new Difference[Stack[A]] {
    override def diff(s1: Stack[A], s2: Stack[A]): String =
      Difference.genericDifference[Stack[A]].diff(s1, s2)
  }
}
