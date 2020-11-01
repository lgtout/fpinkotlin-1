package chapter8.sec4.listing3

import chapter7.sec3.Pars
import chapter7.sec4.map
import chapter8.sec3.listing3.Gen
import chapter8.sec3.listing3.Prop
import java.util.concurrent.Executors
import chapter8.sec3.listing3.unit as sUnit

fun <A> forAll(ga: Gen<A>, f: (A) -> Boolean): Prop = TODO()
val listing2 = {
    //tag::init[]
    val es = Executors.newCachedThreadPool()
    val p1 = forAll(sUnit(Pars.unit(1))) { pi ->
        map(pi, { it + 1 })(es).get() == Pars.unit(2)(es).get()
    }
    //end::init[]
}
