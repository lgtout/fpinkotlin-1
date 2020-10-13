package chapter6.exercises

import chapter3.Cons
import chapter3.List
import chapter3.List.Companion.empty
import chapter6.RNG
import chapter6.rng1
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * TODO: Re-enable tests by removing `!` prefix!
 */
class Exercise_6_4 : WordSpec({

    //tag::init[]
    fun ints(count: Int, rng: RNG): Pair<List<Int>, RNG> =
        when (count) {
            0 -> Pair(empty(), rng)
            else -> {
                val (n, rng1) = rng.nextInt()
                val (l, rng2) = ints(count - 1, rng1)
                Pair(Cons(n, l), rng2)
            }
        }
    //end::init[]

    "ints" should {
        "generate a list of ints of a specified length" {

            ints(5, rng1) shouldBe
                Pair(List.of(1, 1, 1, 1, 1), rng1)
        }
    }
})
