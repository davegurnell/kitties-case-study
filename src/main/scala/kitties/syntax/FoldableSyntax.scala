package kitties.syntax

import kitties.{Foldable, Monoid}

trait FoldableSyntax {
  implicit class FoldableOps[L[_], A](la: L[A]) {
    def foldLeft[B](b: B)(f: (B, A) => B)(implicit foldable: Foldable[L]): B =
      foldable.foldLeft(la, b)(f)

    def foldRight[B](b: B)(f: (A, B) => B)(implicit foldable: Foldable[L]): B =
      foldable.foldRight(la, b)(f)

    def combineAll[B](implicit foldable: Foldable[L], monoid: Monoid[A]): A =
      foldable.combineAll(la)

    def foldMap[B](f: A => B)(implicit foldable: Foldable[L], monoid: Monoid[B]): B =
      foldable.foldMap(la)(f)
  }
}
