# Kitties

In this workshop we'll implement a subset of Cats to understand more deeply how the library is structured and how it works. Our implementation will include type classes for `Monoid`, `Functor`, `Applicative`, `Monad`, `Foldable`, `Traverse`, and `Parallel`. Our code will be in the spirit and structure of Cats, but we'll make many simplifications in the interests of time and sanity.

The workshop is divided into four phases, each of which has a corresponding branch in Git. Feel free to check out the branch of your choice to act as a starting point and go from there!

# Part 1: Setup, Monoid

We'll start with a skeleton project that's laid out similarly to Cats:

- The `kitties` package is a facsimile for the `cats` package. It will contain all of the type classes in our library (`Monoid`, `Functor`, `Monad`, `Traverse`, etc).
- The `kitties.instances` package contains all the type class instances, organised by target type. For example, `kitties.instances.IntInstances` contains all type class instances for `Int`.
- The `kitties.syntax` package contains all the syntax/extension methods we'll use with the type classes, organised by type class. For example, `kitties.syntax.MonoidSyntax` provides all of the extension methods for `Monoid` (i.e. the `|+|` method).
- The various `Instances` and `Syntax` traits are mixed together to two mega traits to simplify access: `kitties.instances.AllInstances` and `kitties.syntax.AllSyntax`.
- The mega traits are mixed together into our kitchen sink import, `kitties.implicits`.

Take a moment to look at the directory structure, and the definitions of `Monoid`, its syntax, and its instances.

Finally, define two `Monoid` instances to get warmed up:

- Define a `Monoid` for `String`.
- Define a `Monoid` for `Lists`.

Make sure your definitions are included in `AllInstances`, and add a couple of unit tests to make sure its working. All working? Ok — let's do some harder stuff!

# Part 2: Functor, Applicative, Monad

These three type classes give us an essential set of methods for sequencing operations on context-like data types like `Option`, `List`, `Either`, and `Future`:

| Type class  | Start with | Method  | Parameter | End with  |
| Functor     | F[A]       | map     | A => B    | F[B]      |
| Applicative | A          | pure    | -         | F[A]      |
| Applicative | F[A]       | product | F[B]      | F[(A, B)] |
| Monad       | F[A]       | flatMap | A => F[B] | F[B]      |

(Note: Cats defines an additional method on `Applicative` called `ap`. We're going to skip that method here because `product` will give us everything we need, albeit in a slightly less performant way.)

How would we define these type classes in Scala? Let's consider `Functor` as an example, which represents the `map` method we see on data types like `Option`:

    abstract class Option[+A] {
      def map[B](func: A => B): Option[B] =
        // etc...
    }

We have to do two things to extract this operation out into a type class. First, we need to move the code outside of the `Option` class, and update its definition to take the `Option` as a parameter:

    def map[A, B](option: Option[A])(f: A => B): Option[B]

Second, we need to replace concrete types like `Option` with abstract ones like `F`:

    trait Functor[F[_]] {
      def map[A, B](fa: F[A])(f: A => B): F[B]
    }

Repeat this process to flesh out the definitions of `Applicative` and `Monad`:

    trait Functor[F[_]] {
      def map[A, B](fa: F[A])(f: A => B): F[B]
    }
    
    trait Applicative[F[_]] {
      def pure // etc...
      def product // etc...
    }
    
    trait Monad[F[_]] {
      def flatMap // etc...
    }

These definitions get us most of the way there, but there's a complication. Every monad is an applicative, and every applicative is a functor. This lets us `map` over a monad, for example, as well as `flatMap` over it. We have a couple of ways of encoding these relationships:

1. We can keep each type class completely separate as they are now. This would be inconvenient because we'd often need to write methods that accept multiple implicit parameters (e.g. a `Functor` as well as a `Monad`).
2. We can replicate the methods of the weaker type classes within the definitions of the stronger ones (e.g. adding a `map` method to the definition of `Monad`). This would be problematic in two ways. First, it would introduce redundancy (we'd need to define `option.map` three times). Second, it would remove any built-in compatibility methods requiring a `Functor[Option]` and one requiring a `Monad[Option]` (we'd need to write lots of explicit conversions, each with a small amount of runtime overhead).
3. We can make `Monad` extend `Applicative` and `Applicative` extend `Functor`. This is the option that Cats chooses, and it's the option that we'll choose here.

Modify your code to include these inheritance relationships:

    trait Functor[F[_]] {
      def map[A, B](fa: F[A])(f: A => B): F[B]
    }
    
    trait Applicative[F[_]] extends Functor[F] {
      def pure // etc...
      def product // etc...
    }
    
    trait Monad[F[_]] extends Applicative[F] {
      def flatMap // etc...
    }

Now the type classes are linked, you can write some canonical definitions of stronger type class methods in terms of weaker ones:

- the `map` method of `Monad` can be written in terms `flatMap` and `pure`;
- the `product` method of `Monad` can be written in terms of `flatMap` and `map`.

## Monad Syntax

Define extension methods for `map` and `flatMap`.

Testing these methods will be tricky because most monadic data types in Scala provide their own non-extension versions.

To work around this restriction, we'll define our own trivial monad `Box`. Define this data type in your test code, and define an instance of `Monad` in its companion object:

    case class Box[A](value: A)
    
    object Box {
      implicit val monad: Moand[Box] =
        // ...
    }

`Box` has no explicit `map` and `flatMap` methods of its own`

Use this data type to write some simple tests for your `map` and `flatMap`.

## Applicative Syntax

Cats' syntax for `Applicative` is not quite 1:1 with the underlying methods. It provides two main extension methods on tuples of values:

    val value1: Foo[String] = // ...
    val value2: Foo[Int] = // ...
    
    val tupled: Foo[(String, Int)] =
      (value1, value2).tupled
    
    val mapped: Foo[Bar] =
      (value1, value2).mapN((str, num) => /* ... */)

Cats provides implementations of these methods for tuples of 2 to 22 fields. We probably won't have time for that, so we'll focus on pairs. Define `tupled` and `mapN` extension methods in terms of `product` and `map`, and write some simple unit tests to try them out.

## More Instances

Define instances of `Monad` for `Option` and `List`. In each case write a couple of unit tests to verify that your implementations work.

Then define an instance for `Future`. Note that you'll need to "inject" an `ExecutionContext` to create the instance:

    implicit def futureMonad(implicit ec: ExecutionContext): Monad[Future] =
      // ...

Finally, if you're feeling bold, define an instance of `Monad` for `Either`. You'll need to use the Kind Projector plugin (included in `build.sbt` already) to "fix" one of the type parameters on `Either` and allow the other to vary:

    implicit def eitherMonad[E]: Monad[Either[E, *]] =
      // ...

This definition will allow you to sequence computations on `Eithers` provided that the type on the left is always the same. This is convenient if you're happy to use the left for some standard error type (e.g. `String` or `List[String]`).

## The Id Monad

Optional. Define an instance of `Monad` for the `Id` type alias defined in `kitties/package.scala`:

    package object kitties {
      type Id[A] = A
    }

This might seem weird, but follow the types and it should all shake out ok. Having this instance allows you to use the same code on monadic and non-monadic contexts:

    val a: Id[Int] = 1
    val b: Id[Int] = 2
    
    val c = for {
      a <- a
      b <- b
    } yield a + b

This is useful for testing monadic code without an actual monad to make things complicated.

# Part 3: Foldable, Traverse

## Foldable

`Foldable` is a type class that embodies the general concepts of folding left and right over sequences. We can define a simple version of the type class as follows:

    trait Foldable[L[_]] {
      def foldLeft[A, B](la: L[A], b: B)(f: (B, A) => B): B
    
      def foldRight[A, B](la: L[A], b: B)(f: (A, B) => B): B
    
      // ... more methods will go here in a bit ...
    }

When defining an instance of `Foldable` we need to be careful to write stack-safe implementations of `foldLeft` and `foldRight` . This is typically easy to do for `foldLeft` , but `foldRight` tends to be harder. Cats defines `foldRight` in terms of a custom data type called `Eval` that guarantees stack safety, but we're skipping that here for simplicity.

Define an instance of `Foldable` for `List`. This should be easy because you can lean on `List's`  built-in methods. You'll be pleased to know that the built-in definition of `list.foldRight` is stack safe, so we don't need to worry about doing anything special.

We can use `foldLeft` and `foldRight` as the basis of a number of simpler, higher-level operations for sequences`Monoids` is. Add the `Foldable` type class to your library and implement the following additional methods in terms of `foldLeft`:

    trait Foldable[L[_]] {
      // ... original code goes here ...
    
      // Fold over a sequence of values,
      // adding them up with the supplied monoid:
      def combineAll[A](la: L[A])(implicit monoid: Monoid[A]): A =
        ???
    
      // Fold over a sequence of values of type A,
      // transforming each to a value of type B
      // and adding the results using a Monoid.
      //
      // Tip: Recognise the name of this method?
      // "Reduce" is another word for "fold",
      // so another name for this would be "mapReduce"!
      def foldMap[A, B](la: L[A])(f: A => B)(implicit monoid: Monoid[B]): B =
        ???
    }

Define extension methods for `combineAll` and `foldMap` and write some unit tests to try them out with your `Foldable` for `List`.

## Traverse

Cats defines a second, higher-level type class for iterating over sequences. This type class, called `Traverse`, defines two more methods to add to our toolkit:

    trait Traverse[L[_]] extends Foldable[L] {
      // Turn an L[F[A]] into a F[L[A]]:
      def sequence[F[_], A](lfa: L[F[A]])(implicit app: Applicative[F]): F[L[A]]
    
      // Traverse `la`, calling `f` for each element
      // and combining the results using the supplied `Applicative`:
      def traverse[F[_], A, B](la: L[A])(f: A => F[B])(implicit app: Applicative[F]): F[L[B]]
    
    }

Conceptually these are similar to the `Future.traverse` and `Future.sequence` methods in `scala.concurrent`, except that:

- they work with any applicative type `F`, not just `Future`;
- the methods from `scala.concurrent` run the `Futures` concurrently... `Traverse` delegates these semantics to the methods in `Applicative`.

These methods are incredibly useful. Any time we have a `List` of `Futures`, we can turn it into a `Future` of a `List` using `sequence`:

    val ids: List[Int] =
      // ...
    
    def lookup(id: Int): Future[Record] =
      // ...
    
    val futures: List[Future[Record]] =
      ids.map(lookup)
    
    val records: Future[List[Record]] =
      futures.sequence

Any time you see a `map` followed by a `sequence`, you can replace it with a call to `traverse`:

    val ids: List[Int] =
      // ...
    
    def lookup(id: Int): Future[Record] =
      // ...
    
    val records: Future[List[Record]] =
      ids.traverse(lookup)

## Traversing Lists

Extend your instance of `Monad[List]` to provide implementations for `Traverse` as well. You should be able to implement `sequence` and `traverse` in terms of `list.foldLeft` and `List.empty` and the methods from `Applicative`.

Create extension methods to enable syntax like `list.traverse(func)` and `list.sequence`, and write some tests for your instance. Note that you may have to separate your extension method for `sequence` into its own `implicit class`.

Finally, when you've got everything else working, try the following for bonus points. The `sequence` method has a canonical definition in terms of `traverse`. See if you can work it out what it is!

# Part 4: Parallel

`Parallel` is a type class that allows "parallel composition" of monadic contexts, by first converting them to a corresponding applicative. This lets us do some things we can't with a regular monad because it would break the implementation of `product`:

- Combine `Eithers` accumulating errors
- Combine `Futures` that run concurrently

## The Type Class

Our version of `Parallel` will be slightly different to Cats' real version. Here's a synopsis:

    // F is our monad:
    trait Parallel[F[_]] {
      // P is some corresponding applicative.
      // It could be the same type as F or it could be a separate type.
      type P[_]
    
      // An applicative for P.
      // This isn't an instance of Monad 
      // so it doesn't have to implement everything in terms of flatMap.
      // Because of this we can be flexible about combination:
      def applicative: Applicative[P]
    
      // Convert an F to a P:
      def toApplicative[A](fa: F[A]): P[A]
    
      // Convert a P back to an F:
      def toMonad[A](pa: P[A]): F[A]
    
      // Combine two Fs by:
      // - converting them to Ps
      // - combining them with our applicative
      // - converting the resulting P back to an F
      def parProduct[A, B](fa: F[A], fb: F[B]): F[(A, B)] =
        ???
    }

## Syntax

Paste the code above into a file in the `kitties` directory and implement the following `ParallelSyntax`.

    // Combine a tuple (F[A], F[B]) into an F[(A, B)]:
    (fa, fb).parTupled
    
    // Combine a tuple (F[A], F[B]) into a single F,
    // and map the result through a function:
    (fa, fb).parMapN(_ + _)

Cats provides overloads for these methods for tuples of 2 to 22 elements. You only have to do it for tuples of size 2.

## Accumulating Errors with Either

Now implement an instance of `Parallel` that lets you combine instances of `Either` accumulating errors (instead of failing fast). Here's a starting-point for your code. Note that the instance uses a `Monoid` to accumulate errors regardless of the type:

    implicit def eitherParallelInstances[E](implicit monoid: Monoid[E]): Parallel[Either[E, *]] =
        new Parallel[Either[E, *]] {
    			// We're actually going to use the same type for F and P.
          // We'll implement a custom applicative that accumulates errors:
          override type P[A] = Either[E, A]
    
      		// Implement the custom applicative here
          override def applicative: Applicative[Either[E, *]] =
            ???
    
          // Because F and P are the same type,
          // the toApplicative and toMonad methods are no-ops:
          override def toApplicative[A](ma: Either[E, A]): Either[E, A] =
            ma
    
          override def toMonad[A](pa: Either[E, A]): Either[E, A] =
            pa
        }

Write some unit tests to verify the behaviour of your `Parallel` for `Either`.

## Running Futures Concurrently

Next, implement a `Parallel` for that runs `Futures` concurrently. Use a similar code structure to the `Parallel` for `Either`. You'll need to supply an implicit `ExecutionContext` to create the instance.

Writing unit tests for concurrent execution is harder than it seems. Try testing your `Future` code on the console using `Thread.sleep` with a long delay in it.

## Combining Parallel with Foldable and Traverse

Finally, add the following methods that combine Parallel with Foldable and Traverse. These are some really powerful high-level operations. Implement these methods, add some corresponding syntax, and test them with `Either` and `Future`:

    trait Parallel[F[_]] {
      // ... add the following below the original code ...
      
      def parTraverse[L[_], A, B](la: L[A])(f: A => F[B])(implicit traverse: Traverse[L]): F[L[B]] =
        ???
    
      def parSequence[L[_], A](lfa: L[F[A]])(implicit traverse: Traverse[L]): F[L[A]] =
        ???
    
      def parFoldMap[L[_], A, B](la: L[A])(f: A => F[B])(implicit foldable: Foldable[L], monoid: Monoid[B]): F[B] = 
        ???
    }

# Extras

Here are a couple of tasks you can complete if you finish everything above:

- Introduce the `sbt-boilerplate` plugin to generate the code for the methods `tupled`, `mapN`, `parTupled`, and `parMapN` for tuples from 2 to 22 fields.