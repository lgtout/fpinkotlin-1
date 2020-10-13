package chapter6.exercises

import chapter6.Rand
import chapter6.rng1
import chapter6.solutions.nonNegativeInt
import chapter6.unit
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * TODO: Re-enable tests by removing `!` prefix!
 */
class Exercise_6_8 : WordSpec({

    //tag::init[]
    fun <A, B> flatMap(f: Rand<A>, g: (A) -> Rand<B>): Rand<B> = {
        rng ->
        val (a, rng2) = f(rng)
        g(a)(rng2)
    }

    //end::init[]

    fun nonNegativeIntLessThan(n: Int): Rand<Int> =
        flatMap(::nonNegativeInt) { a ->
            val mod = a % n
            if (a + (n - 1) - mod >= 0) unit(a)
            else { nonNegativeIntLessThan(n) }
        }

    "flatMap" should {
        "pass along an RNG" {

            val result =
                flatMap(
                    unit(1),
                    { i -> unit(i.toString()) })(rng1)

            result.first shouldBe "1"
            result.second shouldBe rng1
        }
    }

    "nonNegativeIntLessThan" should {
        "return a non negative int less than n" {

            val result =
                nonNegativeIntLessThan(10)(rng1)

            result.first shouldBe 1
        }
    }
})
