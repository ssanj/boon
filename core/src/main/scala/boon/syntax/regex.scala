package boon
package syntax

import boon.model.Predicate
import boon.model.AssertionData

import scala.util.matching.Regex

object regex {

  final class StringSyntax(value: => String) {
    def =^=(reg: Regex): PredicateSyntaxEx =
      new PredicateSyntaxEx {
        override def |(name: => String, ctx: (String, String)*): AssertionData = {
          val result: Predicate[Boolean] = reg.findFirstIn(value).fold(false)(_ => true)
          val diff = one(s"'${value}' did not match regex: /${reg}/")

          result >> diff | (name, ctx:_*)
        }
      }
  }

  implicit def stringToStringSyntax(value1: => String): StringSyntax = new StringSyntax(value1)
}