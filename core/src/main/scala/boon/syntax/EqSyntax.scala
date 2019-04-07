package boon
package syntax

import boon.model.Predicate
import boon.model.IsEqual
import boon.model.IsNotEqual

//
// Operator Precedence: https://docs.scala-lang.org/tour/operators.html
//
// (characters not shown below)
// * / %
// + -
// :
// = !
// < >
// &
// ^
// |
// (all letters)
//

final class EqSyntax[A](value1: => A) {
  def =?=(value2: => A): Predicate[A] = new Predicate[A]((defer(value1), defer(value2)), IsEqual, noHints)

  def =/=(value2: => A): Predicate[A] = new Predicate[A]((defer(value1), defer(value2)), IsNotEqual, noHints)
}
