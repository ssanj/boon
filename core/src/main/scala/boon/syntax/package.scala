package boon

import model.StringRep
import model.Plain

package object syntax {

  def errorTemplate[A: StringRep, B: StringRep](expected: A, got: B): String = {
    s"expected ${expected.strRep} got: ${got.strRep}"
  }

  def plain(value: => String): Plain = Plain(value)

  def input[A: StringRep](value: => A): (String, String) = ("value" -> value.strRep)
  
  def inputM[A: StringRep](value: => A): Map[String, String] = Map("value" -> value.strRep)
}

