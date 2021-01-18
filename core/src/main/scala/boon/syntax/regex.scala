package boon
package syntax

import boon.model.Predicate
import boon.data.NonEmptySeq
import boon.model.AssertionData

import scala.util.matching.Regex
import collection._

object regex {

  final class StringSyntax(value: => String) {
    def xyz(reg: Regex): PredicateSyntax =
      new PredicateSyntax {
        override def |(name: => String, ctx: (String, String)*)(implicit loc: SourceLocation): AssertionData = {
          val result: Predicate[Boolean] = reg.findFirstIn(value).fold(false)(_ => true)
          val diff = one(s"'${value}' did not match regex: /${reg}/")

          result.>> (diff, Replace).| (name, ctx:_*)
        }
      }

    def xyz(reg: Regex, first: String => AssertionData, rest: (String => AssertionData)*): PredicateSyntax = new PredicateSyntax {
      override def |(name: => String, ctx: (String, String)*)(implicit loc: SourceLocation): AssertionData = {
        val it = reg.findAllMatchIn(value)
        if (it.hasNext) {
          NonEmptySeq.fromVector(it.next().subgroups.toVector).
          fold[AssertionData](
            invalid(s"regex: /${reg}/ did not have any subgroups for '${value}'").| (name, ctx:_*)) {
            positional[String](_, name)(NonEmptySeq(first, rest.toVector))
          }
        } else invalid(s"'${value}' did not match regex: /${reg}/").| (name, ctx:_*)
      }
    }
  }

  implicit def stringToStringSyntax(value1: => String): StringSyntax = new StringSyntax(value1)
}