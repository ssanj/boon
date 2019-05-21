package boon
package syntax

import boon.model.AssertionData

trait PredicateSyntaxEx {
  def |(name: => String, ctx: (String, String)*): AssertionData
}
