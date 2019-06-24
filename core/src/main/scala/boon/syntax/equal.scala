package boon
package syntax

import boon.model.StringRep
import model.AssertionData

object equal {

  def isSame[A: BoonType](value1: => A)(implicit loc: SourceLocation): A => AssertionData = 
    value2 => value1 =?= value2 | s"${value1.strRep} is same as ${value2.strRep}"

  def isDiff[A: BoonType](value1: => A)(implicit loc: SourceLocation): A => AssertionData = 
    value2 =>  value1 =/= value2 | s"${value1.strRep} is different to ${value2.strRep}"

  trait DualTypeEquality[A, B] {
    def =?=(f: (A,B) => Boolean)(implicit AS: StringRep[A], BS: StringRep[B]): PredicateSyntax
  }

  implicit class DualTypeEqualitySyntax[A](valueA: => A) { 
    def =>=[B](valueB: => B) = new DualTypeEquality[A, B] {
      def =?=(f:(A, B) => Boolean)(implicit AS: StringRep[A], BS: StringRep[B]): PredicateSyntax = new PredicateSyntax {
        override def |(name: => String, ctx: (String, String)*)(implicit loc: SourceLocation): AssertionData = {
          f(valueA, valueB) >> (one(s"${valueA.strRep} can't be equated to ${valueB.strRep}"), Replace) | (name, ctx:_*)
        }
      }

      def =?=(f:(A, B) => AssertionData): AssertionData = f(valueA, valueB)
    }
  }
}