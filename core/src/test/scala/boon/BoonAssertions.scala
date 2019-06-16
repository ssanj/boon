package boon

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

import scala.collection.SortedMap

private[boon] object BoonAssertions {

  final case class Expected(value: String)
  final case class Got[A](value: A)
  final case class Desc(value: String)

  def failWith[A](expected: Expected, other: => Got[A], assertionDesc: Desc): AssertionData =
    fail(s"Expected ${expected.value} but got ${other.value}") | assertionDesc.value

  def assertAssertionResultPassed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultPassed(AssertionTriple(AssertionName(aName), _, _))) =>
          aName =?= assertionName | s"assertion name of '$assertionName'"
        case other => failWith(Expected("SingleAssertionResult/AssertionResultPassed"), Got(other), Desc("assertion result type"))
      }
    }

  def assertSequentialPass(assertionName: String)(sp: SequentialPass): AssertionData = {
    sp.name.value =?= assertionName | s"assertion name of $assertionName"
  }

  def assertAssertionResultFailed(assertionName: String)(ar: AssertionResult): AssertionData = {
      ar match {
        case SingleAssertionResult(AssertionResultFailed(AssertionError(Assertion(AssertionName(aName), _, _, _), _))) =>
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

  def seqElements1[A](elements: Seq[A], prefix: => String)(f1: A => AssertionData): AssertionData = {
    elements.length =?= 1 | s"$prefix has 1 element" and
    %@(elements) { els =>
      %@(els(0), s"${prefix}(0)") { e1 => f1(e1) }
    }
  }

  def mapElements2[A: Ordering, B](elements: Map[A, B], prefix: => String)(f1: (A, B) => AssertionData, f2: (A, B) => AssertionData): AssertionData = {
    if (elements.size != 2) {
      elements.size =?= 2 | s"${prefix} has 2 elements"
    } else {
      elements.size =?= 2 | s"${prefix} has 2 elements" and
      %@(SortedMap.apply[A, B](elements.toVector:_*).toVector) { els =>
        %@(els(0), s"${prefix}(0)") { Function.tupled(f1) } and
        %@(els(1), s"${prefix}(1)") { Function.tupled(f2) }
      }
    }
  }  
}