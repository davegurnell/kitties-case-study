package kitties.instances

import kitties.Monoid

trait StringInstances {
  implicit val stringInstances: Monoid[String] =
    new Monoid[String] {
      override def empty: String =
        ""

      override def combine(x: String, y: String): String =
        x + y
    }
}
