package kitties.syntax

import kitties.{Functor, Monoid, Parallel, Traverse}

trait ParallelSyntax {
  implicit class ParallelTuple2Ops[F[_], A, B](fab: (F[A], F[B])) {
    def parTupled(implicit parallel: Parallel[F]): F[(A, B)] = {
      val (fa, fb) = fab
      parallel.parProduct(fa, fb)
    }

    def parMapN[R](f: (A, B) => R)(implicit parallel: Parallel[F], functor: Functor[F]): F[R] = {
      val (fa, fb) = fab
      functor.map(parallel.parProduct(fa, fb)) {
        case (a, b) =>
          f(a, b)
      }
    }
  }

  implicit class ParallelTraverseOps[L[_], A](la: L[A]) {
    def parTraverse[M[_], B](f: A => M[B])(implicit traverse: Traverse[L], parallel: Parallel[M]): M[L[B]] =
      parallel.parTraverse(la)(f)

    def parFoldMap[M[_], B](f: A => M[B])(implicit traverse: Traverse[L], parallel: Parallel[M], monoid: Monoid[B]): M[B] =
      parallel.parFoldMap(la)(f)
  }

  implicit class ParallelSequenceOps[L[_], M[_], A](lma: L[M[A]]) {
    def parSequence(implicit traverse: Traverse[L], parallel: Parallel[M]): M[L[A]] =
      parallel.parSequence(lma)
  }
}
