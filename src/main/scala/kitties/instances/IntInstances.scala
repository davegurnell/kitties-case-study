package kitties.instances

import kitties.Monoid

trait IntInstances {
  implicit val intMonoid: Monoid[Int] =
    new Monoid[Int] {
      override def empty: Int =
        0

      override def combine(x: Int, y: Int): Int =
        x + y
    }
}
