package boon
package syntax

import boon.model.AssertionData

trait PredicateSyntax {
  def |(name: => String, ctx: (String, String)*): AssertionData
}
