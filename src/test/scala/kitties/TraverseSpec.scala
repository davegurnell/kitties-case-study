package kitties

import org.scalatest._
import kitties.implicits._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class TraverseSpec extends WordSpec with Matchers with ScalaFutures {
  "sequence syntax" should {
    "sequence a list of options" in {
      List(Option(1), Option(2)).sequence shouldBe Option(List(1, 2))
    }

    "sequence a list of futures" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      List(Future(1), Future(2)).sequence.futureValue shouldBe List(1, 2)
    }
  }

  "traverse syntax" should {
    "traverse a list of options" in {
      List(1, 2).traverse(a => Option(a * 10)) shouldBe Option(List(10, 20))
    }

    "traverse a list of futures" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      List(1, 2).traverse(a => Future(a * 10)).futureValue shouldBe List(10, 20)
    }
  }
}
