package kitties

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {
  def apply[F[_]](implicit instance: Functor[F]): Functor[F] =
    instance
}
