package boon
package model

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
  def =?=(value2: => A): Predicate[A] = new Predicate[A]((defer(value1), defer(value2)), IsEqual, noErrorOverrides)

  def =/=(value2: => A): Predicate[A] = new Predicate[A]((defer(value1), defer(value2)), IsNotEqual, noErrorOverrides)
}
