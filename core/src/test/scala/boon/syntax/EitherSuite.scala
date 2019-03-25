package example

import boon._
import syntax._
import either._

object EitherSuite extends SuiteLike("EitherSuite") {

  private val t1 = test("rights") {
    10.right[String] =?= 10.right[String]    | "equal right"    and
    10.right[String] =/= 19.right[String]    | "unequal right"  and
    50.right[String] =/= "error1".left[Int]  | "right != left"  and
    right_?[String, Int](_ =/= 10 | "right is ten")(20.right[String])
  }

  private val t2 = test("lefts") {
    "error1".left[Int]     =?= "error1".left[Int] | "equal left"    and
    "some orror".left[Int] =/= "error2".left[Int] | "unequal left"  and
    "error1".left[Int]     =/= 10.right[String]   | "left != right"  and
    left_?[String, Int](_.endsWith("or1")         | "expected Left")("meggaerror1".left[Int])
  }

  override val tests = oneOrMore(t1, t2)
}