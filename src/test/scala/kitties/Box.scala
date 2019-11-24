package kitties

case class Box[A](value: A)

object Box {
  implicit val boxMonad: Monad[Box] =
    new Monad[Box] {
      override def pure[A](a: A): Box[A] =
        Box(a)

      override def flatMap[A, B](fa: Box[A])(f: A => Box[B]): Box[B] =
        f(fa.value)
    }
}