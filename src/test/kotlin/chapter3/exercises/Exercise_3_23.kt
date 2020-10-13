package chapter3.exercises

import chapter3.Cons
import chapter3.List
import chapter3.Nil
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

// tag::startsWith[]
tailrec fun <A> startsWith(l1: List<A>, l2: List<A>): Boolean =
    when (l1) {
        Nil -> when (l2) {
            Nil -> true
            is Cons -> false
        }
        is Cons -> when (l2) {
            Nil -> true
            is Cons -> if (l1.head == l2.head)
                startsWith(l1.tail, l2.tail)
            else false
        }
    }
// end::startsWith[]

// tag::init[]
tailrec fun <A> hasSubsequence_1(xs: List<A>, sub: List<A>): Boolean =
    when {
        startsWith(xs, sub) -> true
        else -> when (xs) {
            Nil -> false
            is Cons -> hasSubsequence_1(xs.tail, sub)
        }
    }

fun <A> hasSubsequence_2(xs: List<A>, sub: List<A>): Boolean {
    tailrec fun go(consecutive: Boolean, xs: List<A>, sub: List<A>): Boolean {
        return when (xs) {
            is Nil -> when (sub) {
                is Nil -> true
                is Cons -> false
            }
            is Cons -> when (sub) {
                is Nil -> true
                is Cons -> if (xs.head == sub.head)
                    go(true, xs.tail, sub.tail)
                else if (!consecutive)
                    go(false, xs.tail, sub)
                else false
            }
        }
    }
    return go(false, xs, sub)
}

fun <A> hasSubsequence(xs: List<A>, sub: List<A>): Boolean =
    hasSubsequence_2(xs, sub)
// end::init[]

class Exercise_3_23 : WordSpec({
    "list subsequence" should {
        "determine if a list starts with" {
            val xs = List.of(1, 2, 3)
            startsWith(xs, List.of(1)) shouldBe true
            startsWith(xs, List.of(1, 2)) shouldBe true
            startsWith(xs, xs) shouldBe true
            startsWith(xs, List.of(2, 3)) shouldBe false
            startsWith(xs, List.of(3)) shouldBe false
            startsWith(xs, List.of(6)) shouldBe false
        }

        "identify subsequences of a list" {
            val xs = List.of(1, 2, 3, 4, 5)
            hasSubsequence(xs, List.of(1)) shouldBe true
            hasSubsequence(xs, List.of(1, 2)) shouldBe true
            hasSubsequence(xs, List.of(2, 3)) shouldBe true
            hasSubsequence(xs, List.of(3, 4)) shouldBe true
            hasSubsequence(xs, List.of(3, 4, 5)) shouldBe true
            hasSubsequence(xs, List.of(4)) shouldBe true

            hasSubsequence(xs, List.of(1, 4)) shouldBe false
            hasSubsequence(xs, List.of(1, 3)) shouldBe false
            hasSubsequence(xs, List.of(2, 4)) shouldBe false
        }
    }
})
