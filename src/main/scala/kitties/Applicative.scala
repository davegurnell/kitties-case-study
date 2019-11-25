package kitties

trait Applicative[F[_]] {
  def pure[A](value: A): F[A]
  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
}
