package boon
package syntax

import boon.model.AssertionData

object types {

  def =:=[A, B](f: => AssertionData)(implicit ev: A =:= B): AssertionData = {
    val _ = ev
    f
  }

  def <:<[A, B](f: => AssertionData)(implicit ev: A <:< B): AssertionData = {
    val _ = ev
    f
  }
}
