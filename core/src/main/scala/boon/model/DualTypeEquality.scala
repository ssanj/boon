package boon
package model

trait DualTypeEquality[A, B] {
  def =>>(f: (A,B) => Boolean)(implicit AS: StringRep[A], BS: StringRep[B]): PredicateSyntax
}