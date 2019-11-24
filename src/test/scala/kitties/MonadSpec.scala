package kitties

import kitties.implicits._
import org.scalatest._

class MonadSpec extends WordSpec with Matchers {
  "map and flatMap syntax" should {
    "sequence computations on boxes" in {
      val ans = for {
        a <- Box(1)
        b <- Box(2)
      } yield a + b

      ans shouldBe Box(3)
    }
  }
}
