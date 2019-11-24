package kitties.syntax

import kitties.Functor

trait FunctorSyntax {
  implicit class FunctorOps[F[_], A](fa: F[A]) {
    def map[B](f: A => B)(implicit functor: Functor[F]): F[B] =
      functor.map(fa)(f)
  }
}
