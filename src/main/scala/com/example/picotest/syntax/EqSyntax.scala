package com.example.picotest.syntax

final case class EqSyntax[A](value1: A) {
  def =?=(value2: A): (A, A) = (value1, value2)
}

object EqSyntax {
  implicit def toEqSyntax[A](value1: A): EqSyntax[A] = EqSyntax[A](value1)
}