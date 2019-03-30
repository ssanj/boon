package boon
package syntax

import scala.util.matching.Regex

object regex {

  final class StringSyntax(value: => String) {

    def ^?(reg: Regex): Boolean = reg.findFirstIn(value).fold(false)(_ => true)
  }

  implicit def stringToStringSyntax(value1: => String): StringSyntax = new StringSyntax(value1)
}

