package chapter7.exercises.ex6

import arrow.core.Tuple4
import chapter7.solutions.sol6.Par
import chapter7.solutions.sol6.Pars.lazyUnit
import chapter7.solutions.sol6.Pars.unit
import chapter7.solutions.sol6.Pars.map
import chapter7.solutions.sol6.Pars.map2
import chapter7.solutions.sol6.Pars.sequence
import chapter7.solutions.sol6.Pars.splitAt

object Listing {
    //tag::init[]
    fun <A> parFilter(
        sa: List<A>,
        f: (A) -> Boolean
    ): Par<List<A>> = map(sequence(
        sa.map { a ->
            lazyUnit { if (f(a)) listOf(a) else emptyList() }
        }
    ), Iterable<Iterable<A>>::flatten)
    //end::init[]

    fun sum2(ints: List<Int>): Par<Int> = { es ->
        if (ints.size <= 1)
            unit(0)(es)
        else {
            val (l, r) =
                ints.splitAt(ints.size / 2)
            val sumL: Par<Int> = sum2(l)
            val sumR: Par<Int> = sum2(r)
            map2(sumL, sumR) { a, b -> a + b }(es)
        }
    }

    fun sum3(ints: List<Int>): Par<Int> =
        fold(ints, 0) { a, b -> a + b}

    fun <A, B> fold(
        ts: List<A>, identity: B, f: (B, B) -> B
    ): Par<B> = { es ->
        if (ts.size <= 1)
            unit(identity)(es)
        else {
            val (l, r) =
                ts.splitAt(ts.size / 2)
            val sumL: Par<B> = fold(l, identity, f)
            val sumR: Par<B> = fold(r, identity, f)
            map2(sumL, sumR, f)(es)
        }
    }

    fun <A : Comparable<A>> max(aa: List<A>): Par<A?> =
        fold<A, A?>(aa, null) { a1, a2 ->
            when {
                a1 == null && a2 == null -> null
                a1 == null -> a2
                a2 == null -> a1
                else -> maxOf(a1, a2)
            }
        }

    fun <A, B, C, D> map3(
        a: Par<A>, b: Par<B>, c: Par<C>, f: (A, B, C) -> D
    ): Par<D> = { es ->
        val pp = map2(a, b) { a, b -> Pair(a, b) }
        map2(pp, c) { ab, c -> f(ab.first, ab.second, c) }(es)
    }

    fun <A, B, C, D, E> map4(
        a: Par<A>, b: Par<B>, c: Par<C>, d: Par<D>, f: (A, B, C, D) -> E
    ): Par<E> = { es ->
        val pab = map2(a, b) { a, b -> Pair(a, b) }
        val pcd = map2(c, d) { c, d -> Pair(c, d) }
        map2(pab, pcd) { a, b ->
            f(a.first, a.second, b.first, b.second)
        }(es)
    }

    fun <A, B, C, D, E, F> map5(
        a: Par<A>, b: Par<B>, c: Par<C>, d: Par<D>,
        e: Par<E>, f: (A, B, C, D, E) -> F
    ): Par<F> = { es ->
        val pab = map2(a, b) { a, b -> Pair(a, b) }
        val pcd = map2(c, d) { c, d -> Pair(c, d) }
        val pabcd = map2(pab, pcd) {
                a, b ->
            Tuple4(a.first, a.second, b.first, b.second)
        }
        map2(pabcd, e) {
                t, e -> f(t.a, t.b, t.c, t.d, e)
        }(es)
    }

    fun <A, B, C, D, E> map4a(a: Par<A>, b: Par<B>, c: Par<C>, d: Par<D>, f: (A, B, C, D) -> E): Par<E> =
        { es ->
            val pde =
                map3(a, b, c) { a, b, c -> { d: D -> f(a, b, c, d) } }
            map2(pde, d) { fde, d -> fde(d) }(es)
        }
}

