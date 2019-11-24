package kitties.syntax

import kitties.{Applicative, Traverse}

trait TraverseSyntax {
  implicit class TraverseOps[F[_], A](fa: F[A]) {
    def traverse[G[_], B](f: A => G[B])(implicit traverse: Traverse[F], applicative: Applicative[G]): G[F[B]] =
      traverse.traverse(fa)(f)
  }

  implicit class SequenceOps[F[_], G[_], A](fga: F[G[A]]) {
    def sequence(implicit traverse: Traverse[F], applicative: Applicative[G]): G[F[A]] =
      traverse.sequence(fga)
  }
}
