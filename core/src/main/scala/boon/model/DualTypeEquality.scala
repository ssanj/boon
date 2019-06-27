package boon
package model

trait DualTypeEquality[A, B] {
  def =>>(f: (A, B) => AssertionData)(implicit ABS: StringRep[(A, B)]): AssertionData
}