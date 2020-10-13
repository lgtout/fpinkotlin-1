package chapter7.exercises.ex5

import arrow.core.extensions.list.foldable.foldLeft
// import chapter7.exercises.ex4.Pars
import chapter7.solutions.sol5.Pars
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

typealias Par<A> = (ExecutorService) -> Future<A>

object Listing {
    //tag::init1[]
    fun <A> sequence(ps: List<Par<A>>): Par<List<A>> =
        { es ->
            ps.foldLeft(Pars.lazyUnit { emptyList<A>() }) {
                    acc, curr ->
                Pars.map2(acc, curr) { la, a ->
                    la + a
                }
            }(es)
        }
    //end::init1[]
}
