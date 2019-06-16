package boon
package syntax

import boon.model.AssertionName
import data.NonEmptySeq
import model.AssertionData
import model.StringRep

object nes {

  def positional[A: StringRep](values: NonEmptySeq[A])(assertions: NonEmptySeq[A => AssertionData]): AssertionData = {
    (values.length =?= assertions.length) >> (
    oneOrMore(
      "length of values is different to assertions",
      s"values length: ${values.length}",
      s"assertions length: ${assertions.length}"
    ), Replace) | s"match lengths of ${values.length}" and
    values.zipWithIndex.zip(assertions).map { 
      case ((v, index), af) => 
        af(v).
        label(name => AssertionName(s"element(${index}) ${name.value}")).
        context(Map(s"expected value at (${index})" -> StringRep[A].strRep(v))) 
    }.context(Map("values" -> toStringKVP[A].strRep(values)))
  }

  private def toStringKVP[A: StringRep]: StringRep[NonEmptySeq[A]] = StringRep.from[NonEmptySeq[A]] { values =>
    values.zipWithIndex.map {
      case (v, index) => s"${index} -> ${StringRep[A].strRep(v)}"
    }.mkString("(", ",", ")")
  }

  def nesElements[A](elements: NonEmptySeq[A], no: Int, f: Seq[A] => AssertionData): AssertionData = {
    elements.length =?= no | "no of elements" and
    %@(elements.toSeq, "element")(f)
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

  def nesElements5[A](elements: NonEmptySeq[A], prefix: => String)(f1: A => AssertionData, f2: A => AssertionData, f3: A => AssertionData, f4: A => AssertionData, f5: A => AssertionData): AssertionData = {
    if (elements.length != 5) {
      elements.length =?= 5 | s"$prefix has 5 elements"
    } else {
      elements.length =?= 5 | s"$prefix has 5 elements" and
      %@(elements.toSeq) { els =>
        %@(els(0), s"${prefix}(0)") { e1 => f1(e1) } and
        %@(els(1), s"${prefix}(1)") { e2 => f2(e2) } and
        %@(els(2), s"${prefix}(2)") { e3 => f3(e3) } and
        %@(els(3), s"${prefix}(3)") { e4 => f4(e4) } and
        %@(els(4), s"${prefix}(4)") { e5 => f5(e5) }
      }
    }
  }  
}
