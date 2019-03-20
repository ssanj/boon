package boon.model

trait BoonType[A] extends Equality[A] with StringRep[A] with Difference[A]

object BoonType {

  def apply[T: BoonType]: BoonType[T] = implicitly[BoonType[T]]
}