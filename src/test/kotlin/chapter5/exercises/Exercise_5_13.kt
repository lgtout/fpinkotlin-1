package chapter5.exercises

import chapter3.List
import chapter3.Nil
import chapter4.None
import chapter4.Option
import chapter4.Some
import chapter5.Cons
import chapter5.Empty
import chapter5.Stream
import chapter5.Stream.Companion.empty
import chapter5.solutions.toList
import chapter5.solutions.unfold
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//tag::init[]
fun <A, B> Stream<A>.map(f: (A) -> B): Stream<B> =
    unfold(this) { s ->
        when (s) {
            is Cons -> Some(Pair(f(s.h()), s.t()))
            else -> None
        }
    }

fun <A> Stream<A>.take(n: Int): Stream<A> =
    unfold(Pair(this, n)) { (s, n) ->
        when (s) {
            is Cons -> {
                when {
                    n > 0 -> Some(Pair(s.h(), Pair(s.t(), n-1)))
                    else -> None
                }
            }
            else -> None
        }
    }

fun <A> Stream<A>.takeWhile(p: (A) -> Boolean): Stream<A> =
    unfold(this) { s ->
        when (s) {
            is Cons -> {
                when (p(s.h())) {
                    true -> Some(Pair(s.h(), s.t()))
                    false -> None
                }
            }
            else -> None
        }
    }

fun <A, B, C> Stream<A>.zipWith(
    that: Stream<B>,
    f: (A, B) -> C
): Stream<C> = unfold(Pair(this, that)) { (a, b) ->
    when {
        a is Cons && b is Cons -> Some(Pair(f(a.h(), b.h()), Pair(a.t(), b.t())))
        else -> None
    }
}

// fun <A, B> foo(a: Option<A>, b: Option<B>, at: Stream<A>, bt: Stream<B>):
//     Option<Pair<Pair<Option<A>, Option<B>>, Pair<Stream<A>, Stream<B>>>> =
//     Some(Pair(Pair(a, b), Pair(at, bt)))

fun <A, B> Stream<A>.zipAll(
    that: Stream<B>
): Stream<Pair<Option<A>, Option<B>>> = unfold(Pair(this, that)) { (a, b) ->
    when {
        // a is Cons && b is Cons -> foo(Some(a.h()), Some(b.h()), a.t(), b.t()))
        a is Cons && b is Cons -> Some(Pair(Pair(Some(a.h()), Some(b.h())), Pair(a.t(), b.t())))
        a is Empty && b is Cons -> Some(Pair(Pair(None, Some(b.h())), Pair(Empty, b.t())))
        a is Cons && b is Empty -> Some(Pair(Pair(Some(a.h()), None), Pair(a.t(), Empty)))
        else -> None
    }
}

//end::init[]

/**
 * Re-enable the tests by removing the `!` prefix!
 */
class Exercise_5_13 : WordSpec({

    "Stream.map" should {
        "apply a function to each evaluated element in a stream" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.map { "${(it * 2)}" }.toList() shouldBe
                List.of("2", "4", "6", "8", "10")
        }
        "return an empty stream if no elements are found" {
            empty<Int>().map { (it * 2).toString() } shouldBe empty()
        }
    }

    "Stream.take(n)" should {
        "return the first n elements of a stream" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.take(3).toList() shouldBe List.of(1, 2, 3)
        }

        "return all the elements if the stream is exhausted" {
            val s = Stream.of(1, 2, 3)
            s.take(5).toList() shouldBe List.of(1, 2, 3)
        }

        "return an empty stream if the stream is empty" {
            val s = Stream.empty<Int>()
            s.take(3).toList() shouldBe Nil
        }
    }

    "Stream.takeWhile" should {
        "return elements while the predicate evaluates true" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.takeWhile { it < 4 }.toList() shouldBe List.of(1, 2, 3)
        }
        "return all elements if predicate always evaluates true" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.takeWhile { true }.toList() shouldBe
                List.of(1, 2, 3, 4, 5)
        }
        "return empty if predicate always evaluates false" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.takeWhile { false }.toList() shouldBe List.empty()
        }
    }

    "Stream.zipWith" should {
        "apply a function to elements of two corresponding lists" {
            Stream.of(1, 2, 3)
                .zipWith(Stream.of(4, 5, 6)) { x, y -> x + y }
                .toList() shouldBe List.of(5, 7, 9)
        }
    }

    "Stream.zipAll" should {
        "combine two streams of equal length" {
            Stream.of(1, 2, 3).zipAll(Stream.of(1, 2, 3))
                .toList() shouldBe List.of(
                Pair(Some(1), Some(1)),
                Pair(Some(2), Some(2)),
                Pair(Some(3), Some(3))
            )
        }
        "combine two streams until the first is exhausted" {
            Stream.of(1, 2, 3, 4).zipAll(Stream.of(1, 2, 3))
                .toList() shouldBe List.of(
                Pair(Some(1), Some(1)),
                Pair(Some(2), Some(2)),
                Pair(Some(3), Some(3)),
                Pair(Some(4), None)
            )
        }
        "combine two streams until the second is exhausted" {
            Stream.of(1, 2, 3).zipAll(Stream.of(1, 2, 3, 4))
                .toList() shouldBe List.of(
                Pair(Some(1), Some(1)),
                Pair(Some(2), Some(2)),
                Pair(Some(3), Some(3)),
                Pair(None, Some(4))
            )
        }
    }
})
