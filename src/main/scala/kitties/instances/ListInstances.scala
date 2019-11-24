package kitties.instances

import kitties.{Monad, Monoid}

trait ListInstances {
  implicit def listInstances0[A]: Monoid[List[A]] =
    new Monoid[List[A]] {
      override def empty: List[A] =
        Nil

      override def combine(x: List[A], y: List[A]): List[A] =
        x ++ y
    }

  implicit def listInstances1: Monad[List] =
    new Monad[List] {
      override def pure[A](a: A): List[A] =
        List(a)

      override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] =
        fa.flatMap(f)
    }
}
