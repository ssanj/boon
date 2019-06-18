package boon
package syntax

import boon.model.AssertionName
import data.NonEmptySeq
import model.AssertionData
import model.StringRep
import scala.collection.immutable.SortedMap

object collection {

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
        context(Map(s"expected value at ${prefix}(${index})" -> StringRep[A].strRep(v))) 
    }.context(Map("values" -> toStringKVP[A](prefix).strRep(values)))
  }

  private def toStringKVP[A: StringRep](prefix: String): StringRep[NonEmptySeq[A]] = StringRep.from[NonEmptySeq[A]] { values =>
    values.zipWithIndex.map {
      case (v, index) => s"${prefix}(${index}) -> ${StringRep[A].strRep(v)}"
    }.mkString("(", ", ", ")")
  }

  def positionalSeq[A: StringRep](values: Seq[A], prefix: => String)(assertions: NonEmptySeq[A => AssertionData]): AssertionData = {
    NonEmptySeq.fromVector(values.toVector).fold({
      false >> (one(s"$prefix is empty"), Replace) | s"${prefix} has length of ${assertions.length}"
    }){ elements => 
      positional[A](elements, prefix)(assertions)
    }
  }
  
  def positionalMap[A: Ordering : StringRep, B: StringRep](valuesMap: Map[A, B], prefix: => String)(assertions: NonEmptySeq[(A, B) => AssertionData]): AssertionData = {
    NonEmptySeq.fromVector(SortedMap(valuesMap.toSeq:_*).toVector).fold({
      false >> (one(s"$prefix is empty"), Replace) | s"${prefix} has length of ${assertions.length}"
    }){ elements =>
      positional[(A, B)](elements, prefix)(assertions.map(f => Function.tupled(f)))
    }
  } 
}
