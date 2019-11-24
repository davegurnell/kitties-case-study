package kitties.syntax

import kitties.Monoid

trait MonoidSyntax {
  def empty[A](implicit monoid: Monoid[A]): A =
    monoid.empty

  implicit class MonoidOps[A](x: A) {
    def |+| (y: A)(implicit monoid: Monoid[A]): A =
      monoid.combine(x, y)
  }
}
