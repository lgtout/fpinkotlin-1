package chapter3.exercises

import chapter3.Cons
import chapter3.List
import chapter3.solutions.foldRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

// tag::init[]
fun <A, B> flatMap(xa: List<A>, f: (A) -> List<B>): List<B> =
    foldRight(xa, List.empty()) { a, b ->
        // foldRight(f(a), b) { a, b -> Cons(a, b) }
        append(f(a), b)
    }
// end::init[]

class Exercise_3_19 : WordSpec({
    "list flatmap" should {
        "map and flatten a list" {
            val xs = List.of(1, 2, 3)
            flatMap(xs) { i -> List.of(i, i) } shouldBe
                List.of(1, 1, 2, 2, 3, 3)
        }
    }
})
