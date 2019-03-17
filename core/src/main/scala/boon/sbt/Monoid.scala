package boon.sbt

trait Monoid[F] {

  def mempty: F

  def mappend(x: F, y: F): F
}