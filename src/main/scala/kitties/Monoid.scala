package kitties

trait Monoid[A] {
  def empty: A
  def combine(x: A, y: A): A
}

object Monoid {
  def apply[A](implicit instance: Monoid[A]): Monoid[A] =
    instance
}
