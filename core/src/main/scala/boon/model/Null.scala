package boon
package model

object Null {
  implicit val nullStringRep = StringRep.from[Null.type](_ => "null")
}

final case class Plain(value: String)

object Plain {
  implicit val plainStringRep = StringRep.from[Plain](_.value)
}