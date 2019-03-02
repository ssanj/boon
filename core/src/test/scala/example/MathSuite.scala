package example

import boon._
import syntax._

object MathSuite extends SuiteLike("MathSuite") {

  val multTable =
    NonEmptyMap.values(
      (1, 4)   -> tval(4),
      (2, 6)   -> tval(12),
      (5, 10)  -> tval(50),
      (7, 7)   -> tval(49),
      (-2, -1) -> tval(2),
      (10, 20) -> tval(200)
    )

  val t1 = test("Addition") {
    (2 + 1     =?= 1 + 2       | "commutative")    &
    (2 + 1 + 3 =?= 2 + (1 + 3) | "associative")    &
    (0 + 5     =?= 5           | "left identity")  &
    (5 + 0     =?= 5           | "right identity") &
    (3         =/= (3 + 2)     | "valid")
  }


  val t2 = table[(Int, Int), Int]("Multiplication", multTable)(n => n._1 * n._2)

  private def factorial(n: Int): Int =
    if (n <= 0) 1 else n * factorial(n - 1)

  val t3 = test("factorial") {
    (factorial(1)  =?= 1       | "of 1 is 1")        &
    (factorial(-1) =?= 1       | "of -1 is 1")       &
    (factorial(5)  =?= 120     | "of 5 is 120")      &
    (factorial(4)  =/= 120     | "of 4 is not 120")  &
    (factorial(10) =?= 3628800 | "of 10 is 3628800")
  }

  val t4 = test("1 to x") {
    (1 to 6000).flatMap(x => (1 to x).toList).length =?= 18003000 | "6k"
  }

  val tests = NonEmptySeq.nes(t1, t2, t3, t4)
}