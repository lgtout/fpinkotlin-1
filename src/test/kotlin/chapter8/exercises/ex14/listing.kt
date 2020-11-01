package chapter8.exercises.ex14

import chapter8.sec3.listing3.Prop
import chapter8.sec3.listing3.choose
import chapter8.sec4.listing1.run

val smallInt = choose(-10, 10)

fun List<Int>.prepend(i: Int) = listOf(i) + this

val maxProp: Prop = TODO()

fun main() {
    run(maxProp)
}
