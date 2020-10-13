package chapter6.exercises

import chapter3.Cons
import chapter3.List
import chapter3.exercises.Exercise_3_7.foldRight
import chapter6.RNG
import chapter6.rng1
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//tag::init[]
data class State<S, out A>(val run: (S) -> Pair<A, S>) {

    companion object {
        fun <S, A> unit(a: A): State<S, A> = State { Pair(a, it) }

        fun <S, A, B, C> map2(
            ra: State<S, A>,
            rb: State<S, B>,
            f: (A, B) -> C
        ): State<S, C> = State { s ->
            val (a, s1) = ra.run(s)
            val (b, s2) = rb.run(s1)
            Pair(f(a, b), s2)
        }

        fun <S, A> sequence(fs: List<State<S, A>>): State<S, List<A>> =
            foldRight(
                fs, unit(List.empty())
            ) { ra, rb ->
                map2(ra, rb) { a, b -> Cons(a, b) }
            }
    }

    fun <B> map(f: (A) -> B): State<S, B> = State { s ->
        val (a, s1) = run(s)
        Pair(f(a), s1)
    }

    fun <B> flatMap(f: (A) -> State<S, B>): State<S, B> = State { s ->
        val (a, s1) = run(s)
        f(a).run(s1)
    }

}
//end::init[]

/**
 * TODO: Re-enable tests by removing `!` prefix!
 */
class Exercise_6_10 : WordSpec({
    "unit" should {
        "compose a new state of pure a" {
            State.unit<RNG, Int>(1).run(rng1) shouldBe Pair(1, rng1)
        }
    }
    "map" should {
        "transform a state" {
            State.unit<RNG, Int>(1)
                .map { it.toString() }
                .run(rng1) shouldBe Pair("1", rng1)
        }
    }
    "flatMap" should {
        "transform a state" {
            State.unit<RNG, Int>(1)
                .flatMap { i ->
                    State.unit<RNG, String>(i.toString())
                }.run(rng1) shouldBe Pair("1", rng1)
        }
    }
    "map2" should {
        "combine the results of two actions" {

            val combined: State<RNG, String> =
                State.map2(
                    State.unit(1.0),
                    State.unit(1)
                ) { d: Double, i: Int ->
                    ">>> $d double; $i int"
                }

            combined.run(rng1).first shouldBe ">>> 1.0 double; 1 int"
        }
    }
    "sequence" should {
        "combine the results of many actions" {

            val combined: State<RNG, List<Int>> =
                State.sequence(
                    List.of(
                        State.unit(1),
                        State.unit(2),
                        State.unit(3),
                        State.unit(4)
                    )
                )

            combined.run(rng1).first shouldBe List.of(1, 2, 3, 4)
        }
    }
})
