package boon
package syntax

import either._

object EitherSuite extends SuiteLike("EitherSuite") {

  private val t1 = test("rights") {
    10.right[String] =?= 10.right[String]    | "equal right"    and
    10.right[String] =/= 19.right[String]    | "unequal right"  and
    50.right[String] =/= "error1".left[Int]  | "right != left"  and
    right_?[String, Int](20.right[String])(_ =/= 10 | "twenty is not ten") and
    right_?[String, Boolean](false.right[String])(_ => pass | "is right")
  }

  private val t2 = test("lefts") {
    "error1".left[Int]     =?= "error1".left[Int] | "equal left"     and
    "some orror".left[Int] =/= "error2".left[Int] | "unequal left"   and
    "error1".left[Int]     =/= 10.right[String]   | "left != right"  and
    left_?[String, Int]("meggaerror1".left[Int])(_.endsWith("or1") | "ends with 'or1'") and
    left_?[String, Boolean]("so failed".left[Boolean])(_ => pass | "is left")
  }

  override val tests = oneOrMore(t1, t2)
}