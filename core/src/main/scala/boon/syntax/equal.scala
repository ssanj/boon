package boon
package syntax

import model.AssertionData

object equal {

  def isSame[A: BoonType](value1: => A): A =>  AssertionData = 
    value2 =>  value1 =?= value2 | s"${value1} is same as ${value2}"
}