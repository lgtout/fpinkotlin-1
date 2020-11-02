package chapter8.exercises.ex16

import arrow.core.extensions.list.foldable.forAll
import chapter7.sec4.Par
import chapter7.sec4.fork
import chapter7.sec4.unit
import chapter8.sec3.listing3.Gen
import chapter8.sec3.listing3.choose
import chapter8.sec3.listing3.map
import chapter8.sec4.listing10.forAllPar
import chapter8.sec4.listing9.equal

val listing = {

    val pint: Gen<Par<Int>> =
        choose(0, 10)
            .map { unit(it) }

    forAllPar(pint) { x ->
        equal(fork { x }, x)
    }

    val f = { i: Int -> i < 3 }
    listOf(1, 2, 3).takeWhile(f).forAll(f)
}
