package boon
package syntax

import boon.model.StringRep

final case class StringRepSyntax[T](value: T) {
  def strRep(implicit S: StringRep[T]): String = StringRep[T].strRep(value)
}
