package example

import boon._
import syntax._
import either._

object EitherSuite extends SuiteLike("EitherSuite") {

  private val t1 = test("rights") {
    10.r[String] =?= 10.r[String]    | "equal right"    and
    10.r[String] =/= 19.r[String]    | "unequal right"  and
    50.r[String] =/= "error1".l[Int] | "right != left"  and
    r_?[String, Int](_ =/= 10 | "right is ten")(20.r[String])
  }

  private val t2 = test("lefts") {
    "error1".l[Int]     =?= "error1".l[Int] | "equal left"    and
    "some orror".l[Int] =/= "error2".l[Int] | "unequal left"  and
    "error1".l[Int]     =/= 10.r[String]    | "left != right"  and
    l_?[String, Int](_.endsWith("or1")      | "expected Left")("meggaerror1".l[Int])
  }

  override val tests = oneOrMore(t1, t2)
}