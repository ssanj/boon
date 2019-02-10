package boon

import Boon.defineTest

abstract class SuiteLike(val suiteName: String) {
  import scala.collection.mutable.ListBuffer

  private val testRegister = new ListBuffer[Test]

  def primary: Test

  def suite: Suite = Suite(SuiteName(suiteName), NonEmptySeq(primary, testRegister))

  def test[A](name: String)(gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Unit = {
    testRegister += defineTest[A](name, gen)
  }

  //we need another test method for multi assertion tests
}