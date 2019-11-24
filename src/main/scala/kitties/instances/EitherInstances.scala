package kitties.instances

import kitties.{Applicative, Monad, Monoid, Parallel}

trait EitherInstances {
  implicit def eitherInstances[E]: Monad[Either[E, *]] =
    new Monad[Either[E, *]] {
      override def pure[A](a: A): Either[E, A] =
        Right(a)

      override def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] =
        fa.flatMap(f)
    }

  implicit def eitherParallelInstances[E](implicit monoid: Monoid[E]): Parallel[Either[E, *]] =
    new Parallel[Either[E, *]] {
      override type P[A] = Either[E, A]

      override def applicative: Applicative[Either[E, *]] =
        new Applicative[Either[E, *]] {
          override def pure[A](a: A): Either[E, A] =
            Right(a)

          override def map[A, B](fa: Either[E, A])(f: A => B): Either[E, B] =
            fa.map(f)

          override def product[A, B](fa: Either[E, A], fb: Either[E, B]): Either[E, (A, B)] =
            (fa, fb) match {
              case (Right(x), Right(y)) => Right((x, y))
              case (Right(_), Left(y))  => Left(y)
              case (Left(x), Right(_))  => Left(x)
              case (Left(x), Left(y))   => Left(monoid.combine(x, y))
            }
        }

      override def toApplicative[A](ma: Either[E, A]): Either[E, A] =
        ma

      override def toMonad[A](pa: Either[E, A]): Either[E, A] =
        pa
    }
}
