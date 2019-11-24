package kitties

import kitties.implicits._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class ApplicativeSpec extends WordSpec with Matchers with ScalaFutures {
  "pure syntax" should {
    "create new contexts" in {
      123.pure[Id] shouldBe 123
      123.pure[List] shouldBe List(123)
      123.pure[Option] shouldBe Option(123)
    }

    "create new futures" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      123.pure[Future].futureValue shouldBe 123
    }
  }

  "tupled syntax" should {
    "combine boxes" in {
      (Box("abc"), Box(123)).tupled shouldBe Box(("abc", 123))
    }

    "combine options with short-circuiting semantics" in {
      (Option("abc"), Option(123)).tupled shouldBe Option(("abc", 123))
      (Option("abc"), None).tupled shouldBe None
      (Option.empty[String], Option(123)).tupled shouldBe None
    }

    "combine lists with permutation semantics" in {
      (List("a", "b"), List(1, 2)).tupled shouldBe List(
        ("a", 1),
        ("a", 2),
        ("b", 1),
        ("b", 2)
      )
    }

    "combine futures" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      (Future("a"), Future(1)).tupled.futureValue shouldBe (("a", 1))
    }
  }

  "mapN syntax" should {
    "combine boxes" in {
      (Box(1), Box(2)).mapN(_ + _) shouldBe Box(3)
    }

    "combine options with short-circuiting semantics" in {
      (Option(1), Option(2)).mapN(_ + _) shouldBe Option(3)
      (Option(1), Option.empty[Int]).mapN(_ + _) shouldBe None
      (Option.empty[Int], Option(123)).mapN(_ + _) shouldBe None
    }

    "combine lists with permutation semantics" in {
      (List(10, 20), List(1, 2)).mapN(_ + _) shouldBe List(11, 12, 21, 22)
    }

    "combine futures" in {
      import scala.concurrent.ExecutionContext.Implicits.global

      (Future(1), Future(2)).mapN(_ + _).futureValue shouldBe 3
    }
  }
}
