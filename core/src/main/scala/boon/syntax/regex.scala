package boon
package syntax

import scala.util.matching.Regex

object regex {

  final class StringSyntax(value: => String) {

    def ^?(reg: Regex): DescSyntax[Boolean] =
      reg.findFirstIn(value).fold(false)(_ => true) >> Seq(s"'${value}' did not match regex: /${reg}/")
  }

  implicit def stringToStringSyntax(value1: => String): StringSyntax = new StringSyntax(value1)
}

