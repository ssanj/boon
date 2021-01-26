package boon
package syntax

import boon.model.Predicate
import boon.data.NonEmptySeq
import boon.model.AssertionData

import scala.util.matching.Regex
import collection._

object regex {

  final class RegexNameAware(reg: Regex, value: => String) {
    def |(name: => String)(implicit loc: SourceLocation): AssertionData = {
      val pred: Predicate[Boolean] = reg.findFirstIn(value).fold(false)(_ => true)
      val diff = one(s"'${value}' did not match regex: /${reg}/")

      pred >> differentMessage(diff, Replace) | (name)
    }
  }

  final class RegexGroups(val reg: Regex, first: String => AssertionData, rest: Seq[(String => AssertionData)]) {
    def firstAssertion: String => AssertionData = first
    def remainingAssertions: Seq[(String => AssertionData)] = rest
  }

  final class RegexNameAndGroupsAware(value: => String, groups: RegexGroups) {
    def |(name: => String)(implicit loc: SourceLocation): AssertionData = {
      val reg = groups.reg
      val it = reg.findAllMatchIn(value)
      if (it.hasNext) {
        NonEmptySeq.fromVector(it.next().subgroups.toVector).
        fold[AssertionData](
          invalid(s"regex: /${reg}/ did not have any subgroups for '${value}'") | name) {
          positional[String](_, name)(NonEmptySeq(groups.firstAssertion, groups.remainingAssertions.toVector))
        }
      } else invalid(s"'${value}' did not match regex: /${reg}/") | name
    }
  }

  final class StringSyntax(value: => String) {
    def =^=(reg: Regex): RegexNameAware = new RegexNameAware(reg, value)

    def =^=(groups: RegexGroups): RegexNameAndGroupsAware =
      new RegexNameAndGroupsAware(value, groups)
  }

  def withRegexGroups(reg: Regex, first: String => AssertionData, rest: (String => AssertionData)*): RegexGroups =
    new RegexGroups(reg, first, rest)

  implicit def stringToStringSyntax(value1: => String): StringSyntax = new StringSyntax(value1)
}