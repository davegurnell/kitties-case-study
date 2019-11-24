package kitties

trait Traverse[L[_]] extends Foldable[L] {
  def traverse[F[_], A, B](la: L[A])(f: A => F[B])(implicit app: Applicative[F]): F[L[B]]

  def sequence[F[_], A](lfa: L[F[A]])(implicit app: Applicative[F]): F[L[A]] =
    traverse[F, F[A], A](lfa)(identity)
}

object Traverse {
  def apply[F[_]](implicit instance: Traverse[F]): Traverse[F] =
    instance
}
