package kitties.syntax

import kitties.Monad

trait MonadSyntax {
  implicit class MonadOps[F[_], A](fa: F[A]) {
    def flatMap[B](f: A => F[B])(implicit monad: Monad[F]): F[B] =
      monad.flatMap(fa)(f)
  }
}
