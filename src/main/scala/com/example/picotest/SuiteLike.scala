package com.example.picotest

import Boon.defineTest

abstract class SuiteLike(val suiteName: String) {
  import scala.collection.mutable.ListBuffer

  private val testRegister = new ListBuffer[Test]

  lazy val allTests: Seq[Test] = testRegister

  def test[A](name: String)(gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Unit = {
    testRegister += defineTest[A](name, gen)
  }
}