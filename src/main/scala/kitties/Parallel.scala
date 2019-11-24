package kitties

trait Parallel[M[_]] {
  type P[_]

  def applicative: Applicative[P]

  def toApplicative[A](ma: M[A]): P[A]

  def toMonad[A](pa: P[A]): M[A]

  def parProduct[A, B](ma: M[A], mb: M[B]): M[(A, B)] =
    toMonad(applicative.product(toApplicative(ma), toApplicative(mb)))

  def parTraverse[L[_], A, B](la: L[A])(f: A => M[B])(implicit traverse: Traverse[L]): M[L[B]] =
    toMonad(traverse.traverse(la)(a => toApplicative(f(a)))(applicative))

  def parSequence[L[_], A](lma: L[M[A]])(implicit traverse: Traverse[L]): M[L[A]] =
    parTraverse(lma)(identity)

  def parFoldMap[L[_], A, B](la: L[A])(f: A => M[B])(implicit foldable: Foldable[L], monoid: Monoid[B]): M[B] = {
    val accum0: P[B] =
      applicative.pure(monoid.empty)

    toMonad(foldable.foldLeft(la, accum0) { (b, a) =>
      applicative.map(applicative.product(b, toApplicative(f(a)))) {
        case (b, a) =>
          monoid.combine(b, a)
      }
    })
  }
}
