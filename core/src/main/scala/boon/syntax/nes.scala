package boon
package syntax

import boon.model.AssertionName
import data.NonEmptySeq
import model.AssertionData
import model.StringRep
import scala.collection.SortedMap

object nes {

  def positional[A: StringRep](values: NonEmptySeq[A], prefix: => String)(assertions: NonEmptySeq[A => AssertionData]): AssertionData = {
    (values.length =?= assertions.length) >> (
    oneOrMore(
      s"length of $prefix is different to assertions",
      s"$prefix length: ${values.length}",
      s"assertions length: ${assertions.length}"
    ), Replace) | s"${prefix} has length of ${assertions.length}" and
    values.zipWithIndex.zip(assertions).map { 
      case ((v, index), af) => 
        af(v).
        label(name => AssertionName(s"${prefix}(${index}).${name.value}")).
        context(Map(s"expected value at (${index})" -> StringRep[A].strRep(v))) 
    }.context(Map("values" -> toStringKVP[A].strRep(values)))
  }

  private def toStringKVP[A: StringRep]: StringRep[NonEmptySeq[A]] = StringRep.from[NonEmptySeq[A]] { values =>
    values.zipWithIndex.map {
      case (v, index) => s"${index} -> ${StringRep[A].strRep(v)}"
    }.mkString("(", ",", ")")
  }

  def positional[A: StringRep](values: Seq[A], prefix: => String)(assertions: NonEmptySeq[A => AssertionData]): AssertionData = {
    NonEmptySeq.fromVector(values.toVector).fold({
      false >> (one(s"$prefix is empty"), Replace) | s"${prefix} has length of ${assertions.length}"
    }){ elements => 
      positional[A](elements, prefix)(assertions)
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
