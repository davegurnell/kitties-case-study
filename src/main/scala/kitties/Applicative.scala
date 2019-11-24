package kitties

trait Applicative[F[_]] extends Functor[F] {
  def pure[A](a: A): F[A]

  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
}

object Applicative {
  def apply[F[_]](implicit instance: Applicative[F]): Applicative[F] =
    instance
}
