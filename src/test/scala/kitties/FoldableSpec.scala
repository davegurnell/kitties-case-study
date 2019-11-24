package kitties

import kitties.implicits._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

class FoldableSpec extends WordSpec with Matchers with ScalaFutures {
  "combineAll syntax" should {
    "traverse a list of options" in {
      List(1, 2, 3).combineAll shouldBe 6
    }
  }

  "foldMap syntax" should {
    "combine a list of values" in {
      List(1, 2, 3).foldMap(_.toString) shouldBe "123"
    }
  }
}
