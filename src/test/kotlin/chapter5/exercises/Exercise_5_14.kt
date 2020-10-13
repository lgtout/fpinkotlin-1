package chapter5.exercises

import chapter4.None
import chapter4.Some
import chapter5.Boilerplate.foldRight
import chapter5.Stream
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * Re-enable the tests by removing the `!` prefix!
 */
class Exercise_5_14 : WordSpec({

    //tag::startswith[]
    // TODO Is there a way to implement this that doesn't use foldRight?
    //  That only uses unfold and functions based on unfold?
    fun <A> Stream<A>.startsWith(that: Stream<A>): Boolean =
        zipAll(that).foldRight({ true }) {
                (a, b), acc ->
            when {
                a is Some && b is Some ->
                    if (a.get == b.get) acc()
                    else false
                a is None && b is Some -> false
                else -> true
            }
        }
    //end::startswith[]

    "Stream.startsWith" should {
        "detect if one stream is a prefix of another" {
            Stream.of(1, 2, 3).startsWith(
                Stream.of(1, 2)
            ) shouldBe true
        }
        "detect if one stream is not a prefix of another" {
            Stream.of(1, 2, 3).startsWith(
                Stream.of(2, 3)
            ) shouldBe false
        }
    }
})
