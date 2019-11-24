package kitties

trait Foldable[L[_]] {
  def foldLeft[A, B](la: L[A], b: B)(f: (B, A) => B): B

  def foldRight[A, B](la: L[A], b: B)(f: (A, B) => B): B

  def combineAll[A](la: L[A])(implicit monoid: Monoid[A]): A =
    foldLeft(la, monoid.empty)(monoid.combine)

  def foldMap[A, B](la: L[A])(f: A => B)(implicit monoid: Monoid[B]): B =
    foldLeft(la, monoid.empty)((b, a) => monoid.combine(b, f(a)))
}

object Foldable {
  def apply[F[_]](implicit instance: Foldable[F]): Foldable[F] =
    instance
}
