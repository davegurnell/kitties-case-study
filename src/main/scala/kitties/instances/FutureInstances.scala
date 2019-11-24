package kitties.instances

import kitties.Monad

import scala.concurrent.{ExecutionContext, Future}

trait FutureInstances {
  implicit def futureInstances(implicit ec: ExecutionContext): Monad[Future] =
    new Monad[Future] {
      override def pure[A](a: A): Future[A] =
        Future.successful(a)

      override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] =
        fa.flatMap(f)
    }
}
