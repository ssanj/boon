package boon
package syntax

import boon.model.AssertionData


trait PredicateSyntax {
  def named(name: => String)(implicit loc: SourceLocation): AssertionData
}
