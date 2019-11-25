package kitties.instances

import kitties.Monoid

trait ListInstances {
  implicit def listMonoid[A]: Monoid[List[A]] =
    new Monoid[List[A]] {
      override def empty: List[A] =
        Nil

      override def combine(x: List[A], y: List[A]): List[A] =
        x ++ y
    }
}
