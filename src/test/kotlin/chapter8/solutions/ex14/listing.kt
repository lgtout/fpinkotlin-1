package chapter8.solutions.ex14

import arrow.core.extensions.list.foldable.exists
import chapter8.sec3.listing3.choose
import chapter8.sec3.listing3.forAll
import chapter8.sec3.listing3.forAllSGen
import chapter8.sec3.listing3.sGen
import chapter8.sec4.listing1.run

val smallInt = choose(-10, 10)

fun List<Int>.prepend(i: Int) = listOf(i) + this

//tag::init[]
val maxProp = forAllSGen(sGen(smallInt)) { ns ->
    val nss = ns.sorted()
    nss.isEmpty() or // <1>
            (nss.size == 1) or // <2>
            nss.zip(nss.prepend(Int.MIN_VALUE))
                .foldRight(true, { p, b ->
                    val (pa, pb) = p
                    b && (pa >= pb)
                }) and // <3>
            nss.containsAll(ns) and // <4>
            !nss.exists { !ns.contains(it) } // <5>
}
//end::init[]

fun main() {
    run(maxProp)
}
