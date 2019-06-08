package boon
package syntax

import boon.model.AssertionData
import com.github.ghik.silencer.silent

object types {

  @silent("never used")
  def =:=[A, B](f: => AssertionData)(implicit ev: A =:= B): AssertionData = {
    f
  }

  @silent("never used")
  def <:<[A, B](f: => AssertionData)(implicit ev: A <:< B): AssertionData = {
    f
  }
}
