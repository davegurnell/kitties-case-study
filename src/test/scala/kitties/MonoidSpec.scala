package kitties

import kitties.implicits._
import org.scalatest._

class MonoidSpec extends FlatSpec with Matchers {
  "Monoid[A].empty" should "produce empty values" in {
    Monoid[Int].empty shouldBe 0
    Monoid[Double].empty shouldBe 0.0
    Monoid[String].empty shouldBe ""
    Monoid[List[Int]].empty shouldBe Nil
  }

  "x |+| y" should "add values" in {
    1 |+| 2 shouldBe 3
    1.1 |+| 2.2 shouldBe (3.3 +- 0.000001)
    "hello" |+| "world" shouldBe "helloworld"
    List(1, 2, 3) |+| List(4, 5, 6) shouldBe List(1, 2, 3, 4, 5, 6)
  }
}
