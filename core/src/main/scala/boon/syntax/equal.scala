package boon
package syntax

import model.AssertionData

object equal {

  def isSame[A: BoonType](value1: => A)(implicit loc: SourceLocation): A => AssertionData = 
    value2 => value1 =?= value2 | (s"${value1.strRep} is same as ${value2.strRep}", input(value2))

  def isDiff[A: BoonType](value1: => A)(implicit loc: SourceLocation): A => AssertionData = 
    value2 =>  value1 =/= value2 | (s"${value1.strRep} is different to ${value2.strRep}", input(value2))
}