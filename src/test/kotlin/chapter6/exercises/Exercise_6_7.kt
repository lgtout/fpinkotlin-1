package chapter6.exercises

import chapter3.Cons
import chapter3.List
import chapter3.List.Companion.empty
import chapter3.Nil
import chapter3.exercises.Exercise_3_7.foldRight
import chapter6.RNG
import chapter6.Rand
import chapter6.rng1
import chapter6.solutions.map2
import chapter6.unit
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * TODO: Re-enable tests by removing `!` prefix!
 */
class Exercise_6_7 : WordSpec({

    //tag::init[]
    fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> =
        { rng1 ->
            when (fs) {
                is Nil -> Pair(Nil, rng1)
                is Cons -> {
                    val (n, rng2) = fs.head(rng1)
                    val (t, rng3) = sequence(fs.tail)(rng2)
                    Pair(Cons(n, t), rng3)
                }
            }
        }
    //end::init[]

    //tag::init2[]
    fun <A> sequence2(fs: List<Rand<A>>): Rand<List<A>> = { rng ->
        foldRight(fs, unit(empty<A>())) { ra, rb ->
            map2(ra, rb) { a, b -> Cons(a, b) }
        } (rng)
    }
    //end::init2[]

    fun ints2(count: Int, rng: RNG): Pair<List<Int>, RNG> {
        fun go(count: Int): List<Rand<Int>> =
            when (count) {
                0 -> empty()
                else -> Cons({ rng -> rng.nextInt() }, go(count - 1))
            }
        return sequence2(go(count))(rng)
    }

    "sequence" should {

        "combine the results of many actions using recursion" {

            val combined: Rand<List<Int>> =
                sequence(
                    List.of(
                        unit(1),
                        unit(2),
                        unit(3),
                        unit(4)
                    )
                )

            combined(rng1).first shouldBe
                List.of(1, 2, 3, 4)
        }

        """combine the results of many actions using
            foldRight and map2""" {

            val combined2: Rand<List<Int>> =
                sequence2(
                    List.of(
                        unit(1),
                        unit(2),
                        unit(3),
                        unit(4)
                    )
                )

            combined2(rng1).first shouldBe
                List.of(1, 2, 3, 4)
        }
    }

    "ints" should {
        "generate a list of ints of a specified length" {
            ints2(4, rng1).first shouldBe
                List.of(1, 1, 1, 1)
        }
    }
})
