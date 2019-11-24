package kitties

import org.scalatest.{Matchers, WordSpec}
import kitties.implicits._

class ParallelSpec extends WordSpec with Matchers {
  def ko(e: String *): Either[List[String], Int] =
    Left(e.toList)

  def ok(a: Int): Either[List[String], Int] =
    Right(a)

  "parTupled" should {
    "accumulate errors with either" in {
      (ko("a"), ko("b")).parTupled shouldBe ko("a", "b")
    }
  }

  "parMapN" should {
    "accumulate errors with either" in {
      (ko("a"), ko("b")).parMapN(_ + _) shouldBe ko("a", "b")
    }
  }

  "parSequence" should {
    "accumulate errors with either" in {
      List(ko("a"), ko("b")).parSequence shouldBe ko("a", "b")
    }
  }

  "parTraverse" should {
    "accumulate errors with either" in {
      List("a", "b").parTraverse(msg => ko(msg)) shouldBe ko("a", "b")
    }
  }

  "parFoldMap" should {
    "accumulate errors with either" in {
      List("a", "b").parFoldMap(msg => ko(msg)) shouldBe ko("a", "b")
    }
  }
}
