package kitties.instances

import kitties.{Id, Monad}

trait IdInstances {
  implicit val idInstances1: Monad[Id] =
    new Monad[Id] {
      override def pure[A](a: A): Id[A] =
        a

      override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
        f(fa)
    }
}
