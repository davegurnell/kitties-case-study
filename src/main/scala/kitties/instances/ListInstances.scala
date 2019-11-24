package kitties.instances

import kitties.{Applicative, Monad, Monoid, Traverse}

trait ListInstances {
  implicit def listInstances0[A]: Monoid[List[A]] =
    new Monoid[List[A]] {
      override def empty: List[A] =
        Nil

      override def combine(x: List[A], y: List[A]): List[A] =
        x ++ y
    }

  implicit def listInstances1: Monad[List] with Traverse[List] =
    new Monad[List] with Traverse[List] {
      override def pure[A](a: A): List[A] =
        List(a)

      override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] =
        fa.flatMap(f)

      override def traverse[G[_], A, B](fa: List[A])(f: A => G[B])(implicit app: Applicative[G]): G[List[B]] = {
        val accum0: G[List[B]] =
          app.pure(Nil)

        fa.foldRight(accum0) { (a, accum) =>
          app.map(app.product(f(a), accum)) {
            case (b, bs) =>
              b :: bs
          }
        }
      }

      override def foldLeft[A, B](la: List[A], b: B)(f: (B, A) => B): B =
        la.foldLeft(b)(f)

      override def foldRight[A, B](la: List[A], b: B)(f: (A, B) => B): B =
        la.foldRight(b)(f)
    }
}
