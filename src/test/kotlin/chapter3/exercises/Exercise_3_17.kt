package chapter3.exercises

import chapter3.Cons
import chapter3.List
import chapter3.solutions.foldRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

// tag::init[]
fun <A, B> map(xs: List<A>, f: (A) -> B): List<B> =
    foldRight(xs, List.empty<B>()) { a, b ->
        Cons(f(a), b)
    }
// end::init[]

class Exercise_3_17 : WordSpec({
    "list map" should {
        "apply a function to every list element" {
            map(List.of(1, 2, 3, 4, 5)) { it * 10 } shouldBe
                List.of(10, 20, 30, 40, 50)
        }
    }
})
