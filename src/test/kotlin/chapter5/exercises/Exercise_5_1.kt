package chapter5.exercises

import chapter3.List
import chapter3.Nil
import chapter3.exercises.reverse
import chapter3.Cons as LCons
import chapter5.Cons
import chapter5.Empty
import chapter5.Stream
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * Re-enable the tests by removing the `!` prefix!
 */
class Exercise_5_1 : WordSpec({
    //tag::init[]
    fun <A> Stream<A>.toList(): List<A> = when (this) {
        is Cons -> LCons(head(), tail().toList())
        is Empty -> Nil
    }
    fun <A> Stream<A>.toList2(): List<A> {
        tailrec fun go(l: List<A>, s: Stream<A>): List<A> =
            when (s) {
                is Cons -> go(LCons(s.head(), l), s.tail())
                is Empty -> l
            }
        return reverse(go(Nil, this))
    }
    //end::init[]

    "Stream.toList" should {
        "force the stream into an evaluated list" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.toList2() shouldBe List.of(1, 2, 3, 4, 5)
        }
    }
})
