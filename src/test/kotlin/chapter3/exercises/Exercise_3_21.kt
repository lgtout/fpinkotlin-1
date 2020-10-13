package chapter3.exercises

import chapter3.Cons
import chapter3.List
import chapter3.Nil
import chapter3.solutions.foldRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

// tag::init[]
fun add_viaFoldLeft(xa: List<Int>, xb: List<Int>): List<Int> =
    foldLeft(xa, Pair(xb, List.empty<Int>())) {
            b, a ->
        when (val xb2 = b.first) {
            Nil -> b.copy(second = Cons(a, b.second))
            is Cons -> Pair(xb2.tail, Cons(a + xb2.head, b.second))
        }
    }.second.let { reverse(it) }

// foldRight not viable because we can't assume the two lists have the same
// length so that the last elements of both align :(
// fun add_viaFoldRight(xa: List<Int>, xb: List<Int>): List<Int> =
//     foldRight(xa, reverse(xb)) {
//             a, b ->
//     }.second.let { reverse(it) }

fun add_viaExplicitRecursion(xa: List<Int>, xb: List<Int>): List<Int> =
    when (xa) {
        Nil -> xb
        is Cons -> when (xb) {
            Nil -> xa
            is Cons -> Cons(xa.head + xb.head, add(xa.tail, xb.tail))
        }
    }
val add: (List<Int>, List<Int>) -> List<Int> = ::add_viaExplicitRecursion
// end::init[]

class Exercise_3_21 : WordSpec({
    "list add" should {
        "add elements of two corresponding lists" {
            add(List.of(1, 2, 3), List.of(4, 5, 6)) shouldBe
                List.of(5, 7, 9)
        }
    }
})
