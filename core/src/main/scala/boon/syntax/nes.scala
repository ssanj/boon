package boon
package syntax

import data.NonEmptySeq
import model.AssertionData
import model.StringRep

object nes {

  def positional[A: StringRep](values: NonEmptySeq[A])(assertions: NonEmptySeq[A => AssertionData]): AssertionData = {
    (values.length =?= assertions.length) >> (
    oneOrMore(
      "lengths of values is different to assertions",
      s"values length: ${values.length}",
      s"assertions length: ${assertions.length}"
    ), Replace) | "match lengths" and
    values.zipWithIndex.zip(assertions).map { 
      case ((v, index), af) => af(v).context(Map(s"expected value at (${index})" -> StringRep[A].strRep(v))) 
    }.context(Map("values" -> values.strRep))
  }
}
