package boon
package syntax

import model.StringRep
import model.AssertionData

object equal {

  def isSame[A: BoonType](value1: => A): A =>  AssertionData = 
    value2 =>  value1 =?= value2 | s"${StringRep[A].strRep(value1)} is same as ${StringRep[A].strRep(value2)}"
}