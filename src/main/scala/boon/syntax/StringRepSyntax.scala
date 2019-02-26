package boon
package syntax

final case class StringRepSyntax[T](value: T) {
  def strRep(implicit S: StringRep[T]): String = StringRep[T].strRep(value)
}
