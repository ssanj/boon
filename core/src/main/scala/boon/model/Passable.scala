package boon
package model

sealed trait Passable
case object Passed extends Passable
case object Failed extends Passable

object Passable {
  def hasPassed(passable: Passable): Boolean = passable == Passed

  def hasFailed(passable: Passable): Boolean = !hasPassed(passable)

  implicit val passableBoonType = BoonType.defaults[Passable]
}