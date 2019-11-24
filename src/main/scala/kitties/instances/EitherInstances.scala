package kitties.instances

import kitties.Monad

trait EitherInstances {
  implicit def eitherInstances[E]: Monad[Either[E, *]] =
    new Monad[Either[E, *]] {
      override def pure[A](a: A): Either[E, A] =
        Right(a)

      override def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] =
        fa.flatMap(f)
    }
}
