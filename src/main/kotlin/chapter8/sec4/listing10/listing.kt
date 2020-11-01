package chapter8.sec4.listing10

import chapter7.sec4.Par
import chapter8.RNG
import chapter8.sec3.listing3.Gen
import chapter8.sec3.listing3.Prop
import chapter8.sec3.listing3.choose
import chapter8.sec3.listing3.forAll
import chapter8.sec3.listing3.map
import chapter8.sec3.listing3.unit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun <A> weighted(
    pga: Pair<Gen<A>, Double>,
    pgb: Pair<Gen<A>, Double>
): Gen<A> = TODO()

fun <A, B, C> map2(ga: Gen<A>, gb: Gen<B>, f: (A, B) -> C): Gen<C> =
    TODO()

//tag::init1[]
val ges: Gen<ExecutorService> = weighted( // <1>
    choose(1, 4).map {
        Executors.newFixedThreadPool(it)
    } to .75, // <2>
    unit<RNG, ExecutorService> (
        Executors.newCachedThreadPool()
    ) to .25) // <3>

fun <A> forAllPar(ga: Gen<A>, f: (A) -> Par<Boolean>): Prop =
    forAll(
        map2(ges, ga) { es, a -> es to a } // <4>
    ) { (es, a) -> f(a)(es).get() }
//end::init1[]
