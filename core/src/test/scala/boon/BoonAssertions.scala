package boon

import boon.data.NonEmptySeq
import boon.model.AssertionResultThrew
import boon.model.AssertionThrow
import boon.model.AssertionName
import boon.model.Assertion
import boon.model.AssertionError
import boon.model.AssertionResultFailed
import boon.model.SequentialPass
import boon.model.AssertionResult
import boon.model.AssertionName
import boon.model.AssertionTriple
import boon.model.AssertionResultPassed
import boon.model.SingleAssertionResult
import boon.model.AssertionData

private[boon] object BoonAssertions {

  final case class Expected(value: String)
  final case class Got[A](value: A)
  final case class Desc(value: String)

  def failWith[A](expected: Expected, other: => Got[A], assertionDesc: Desc): AssertionData =
    fail(s"Expected ${expected.value} but got ${other.value}") | assertionDesc.value

  def assertAssertionResultPassed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(aName), context, _))) =>
          aName =?= assertionName | s"assertion name of '$assertionName'"
        case other => failWith(Expected("SingleAssertionResult/AssertionResultPassed"), Got(other), Desc("assertion result type"))
      }
    }

  def assertSequentialPass(assertionName: String)(sp: SequentialPass): AssertionData = {
    sp.name.value =?= assertionName | s"assertion name of $assertionName"
  }

  def assertAssertionResultFailed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(aName), _, _, _), errors))) =>
          aName =?= assertionName | s"assertion name of $assertionName"
        case other => failWith(Expected("SingleAssertionResult/AssertionResultFailed"), Got(other), Desc("assertion result type"))
      }
    }

  def assertAssertionResultThrew(assertionName: String, f: Throwable => AssertionData)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultThrew(AssertionThrow(AssertionName(aName), throwable, _))) =>
          aName =?= assertionName | s"assertion name of $assertionName" and
          f(throwable)
        case other => failWith(Expected("SingleAssertionResult/AssertionResultThrew"), Got(other), Desc("assertion result type"))
      }
    }

  def nesElements[A](elements: NonEmptySeq[A], no: Int, f: Seq[A] => AssertionData): AssertionData = {
    elements.length =?= no | "no of elements" and
    %@(elements.toSeq, "element")(f)
  }

  def seqElements1[A](elements: Seq[A], prefix: => String)(f1: A => AssertionData): AssertionData = {
    elements.length =?= 1 | s"$prefix has 1 element" and
    %@(elements) { els =>
      %@(els(0), s"${prefix}(0)") { e1 => f1(e1) }
    }
  }

  def nesElements1[A](elements: NonEmptySeq[A], prefix: => String)(f1: A => AssertionData): AssertionData = {
    elements.length =?= 1 | s"$prefix has 1 element" and
    %@(elements.toSeq) { els =>
      %@(els(0), s"${prefix}(0)") { e1 => f1(e1) }
    }
  }

  def nesElements2[A](elements: NonEmptySeq[A], prefix: => String)(f1: A => AssertionData, f2: A => AssertionData): AssertionData = {
    elements.length =?= 2 | s"$prefix has 2 elements" and
    %@(elements.toSeq) { els =>
      %@(els(0), s"${prefix}(0)") { e1 => f1(e1) } and
      %@(els(1), s"${prefix}(1)") { e2 => f2(e2) }
    }
  }

  def nesElements3[A](elements: NonEmptySeq[A], prefix: => String)(f1: A => AssertionData, f2: A => AssertionData, f3: A => AssertionData): AssertionData = {
    elements.length =?= 3 | s"$prefix has 3 elements" and
    %@(elements.toSeq) { els =>
      %@(els(0), s"${prefix}(0)") { e1 => f1(e1) } and
      %@(els(1), s"${prefix}(1)") { e2 => f2(e2) } and
      %@(els(2), s"${prefix}(2)") { e3 => f3(e3) }
    }
  }

  def nesElements4[A](elements: NonEmptySeq[A], prefix: => String)(f1: A => AssertionData, f2: A => AssertionData, f3: A => AssertionData, f4: A => AssertionData): AssertionData = {
    elements.length =?= 4 | s"$prefix has 4 elements" and
    %@(elements.toSeq) { els =>
      %@(els(0), s"${prefix}(0)") { e1 => f1(e1) } and
      %@(els(1), s"${prefix}(1)") { e2 => f2(e2) } and
      %@(els(2), s"${prefix}(2)") { e3 => f3(e3) } and
      %@(els(3), s"${prefix}(3)") { e4 => f4(e4) }
    }
  }
}