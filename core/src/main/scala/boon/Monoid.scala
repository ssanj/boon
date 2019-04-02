package boon

trait Monoid[F] {

  def mempty: F

  def mappend(x: F, y: F): F
}

object Monoid {
  def apply[F: Monoid]: Monoid[F] = implicitly[Monoid[F]]
}