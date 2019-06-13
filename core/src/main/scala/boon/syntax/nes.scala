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
}
