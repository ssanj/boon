package boon
package model

trait DualTypeEquality[A, B] {
  def =>>(f: (A,B) => Boolean)(implicit AS: StringRep[A], BS: StringRep[B]): PredicateSyntax

  def =>>(f: (A, B) => AssertionData)(implicit ABS: StringRep[(A, B)]): AssertionData
}