package boon
package model

final case class Defer[A](value: () => A) {
  def map[B](f: A => B): Defer[B] = Defer(() => f(value()))

  def flatMap[B](f: A => Defer[B]): Defer[B] = Defer(() => f(value()).value())

  def run(): A = value()
}