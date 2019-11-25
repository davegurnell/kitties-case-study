package kitties.instances

import kitties.Monoid

trait TupleInstances {
  implicit def tuple2Monoid[A, B](implicit aMonoid: Monoid[A], bMonoid: Monoid[B]): Monoid[(A, B)] = {
    new Monoid[(A, B)] {
      override def empty: (A, B) = {
        (aMonoid.empty, bMonoid.empty)
      }

      override def combine(x: (A, B), y: (A, B)): (A, B) = {
        val (x1, x2) = x
        val (y1, y2) = y

        (aMonoid.combine(x1, y1), bMonoid.combine(x2, y2))
      }
    }
  }
}
