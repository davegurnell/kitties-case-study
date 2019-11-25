package kitties.instances

import kitties.Monoid

trait DoubleInstances {
  implicit val doubleMonoid: Monoid[Double] =
    new Monoid[Double] {
      override def empty: Double =
        0.0

      override def combine(x: Double, y: Double): Double =
        x + y
    }
}
