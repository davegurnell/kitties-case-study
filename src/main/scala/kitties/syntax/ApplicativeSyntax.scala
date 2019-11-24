package kitties.syntax

import kitties.Applicative

trait ApplicativeSyntax {
  implicit class ApplicativeConstructorOps[A](a: A) {
    def pure[F[_]](implicit applicative: Applicative[F]): F[A] =
      applicative.pure(a)
  }

  implicit class ApplicativeTuple2Ops[F[_], A, B](fab: (F[A], F[B])) {
    def tupled(implicit applicative: Applicative[F]): F[(A, B)] = {
      val (fa, fb) = fab
      applicative.product(fa, fb)
    }

    def mapN[R](func: (A, B) => R)(implicit applicative: Applicative[F]): F[R] = {
      applicative.map(tupled) { case (a, b) => func(a, b) }
    }
  }
}
