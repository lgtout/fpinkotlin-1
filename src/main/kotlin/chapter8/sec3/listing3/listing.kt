package chapter8.sec3.listing3

import arrow.core.getOrElse
import arrow.core.toOption
import chapter8.Falsified
import chapter8.Passed
import chapter8.RNG
import chapter8.Result
import chapter8.SimpleRNG
import chapter8.State
import chapter8.TestCases
import chapter8.double
import chapter8.nonNegativeInt
import kotlin.math.absoluteValue
import kotlin.math.min

typealias Gen<A> = State<RNG, A>
typealias SGen<A> = (Int) -> Gen<A>
typealias MaxSize = Int
typealias Prop = (MaxSize, TestCases, RNG) -> Result

fun main() {
    val p = forAll(
        sGen(Gen { rng -> nonNegativeInt(rng) })
    ) { it.all { i -> i >= 0 } }
    // ) { it::all { i -> i >= 0 } }
    // val r = p.check(2, 5, SimpleRNG(0))
    // val r = p.check(5, 10, SimpleRNG(0))
    // val r = p.check(5, 12, SimpleRNG(0))
    val r = p(3, 5, SimpleRNG(0))
    // val r = p.check(5, 2, SimpleRNG(0))
    // val r = p.check(5, 4, SimpleRNG(0))
    // val r = p.check(5, 0, SimpleRNG(0))
    println(r)
}

fun <A> unit(a: A): Gen<A> = State.unit(a)

/* This is actually not a uniform distribution. If the
range of nonNegativeInt isn't a multiple of (stopExclusive - start),
some numbers will be generated with greater frequency than others. */
fun choose(start: Int, stopExclusive: Int): Gen<Int> =
    State { rng: RNG -> nonNegativeInt(rng) }
        .map { start + (it % (stopExclusive - start)) }

fun <A> listOfN(n: Int, ga: Gen<A>): Gen<List<A>> =
    State.sequence(List(n) { ga })

fun <A> weighted(
    pga: Pair<Gen<A>, Double>,
    pgb: Pair<Gen<A>, Double>
): Gen<A> {
    val (ga, p1) = pga
    val (gb, p2) = pgb
    val prob =
        p1.absoluteValue /
            (p1.absoluteValue + p2.absoluteValue)
    return State { rng: RNG -> double(rng) }
        .flatMap { d ->
            if (d < prob) ga else gb
        }
}

fun <A> sGen(ga: Gen<A>): SGen<List<A>> =
    { listOfN(it, ga) }

//tag::init[]

fun <A> forAll(g: SGen<A>, f: (A) -> Boolean): Prop =
    /* max is the maximum size of each test-case i.e. the size
    of each List<A> that our SGen<List<A>> generates. */
    { max, n, rng ->
        /* When n > max, casePerSize is 1 + n / max.
        Eg. when n = 6 and maxSize is 5, casePerSize is 2.
        What we're trying to do here is evenly distribute the
        test cases into buckets that represent case sizes.  We're
        ok if this results in our test case count exceeding n.
        Eg. When maxSize is 5, we have 5 buckets, over which
        we distribute n = 6 cases.  If we don't have enough cases
        to make sure all the buckets have the same number of cases,
        we increase the number of cases so that is possible.
        With this calculation, we don't need to specify n >= max
        in order to ensure we get at least 1 case per size.  We
        would if instead we had simply n/max.  Rather, n = 1 will
        do just fine. */
        val casePerSize: Int = (n + (max - 1)) / max // <2>
        // val casePerSize: Int = n / max // <2>
        // val casePerSize: Int = (n + max) / max // <2>

        println("max $max")
        println("n $n")
        println("casePerSize $casePerSize")

        val props: Sequence<Prop> =
            generateSequence(0) { it + 1 } // <3>
                /* If n >= max, we select max as the number of size
                buckets and distribute at least one case into
                each bucket.  Otherwise, if n < max, we will only
                have n buckets, and the number of Ints in the test
                case in the bucket for the largest size case be n,
                not max.  We +1 because we want to have a zero-count
                bucket. */
                .take(min(n, max) + 1)
                .map { i ->

                    println("size $i")

                    // Use Gen<List<A>> and (A) -> Boolean to
                    // construct a Prop for the current i-sized
                    // size-bucket.
                    forAll(g(i), f)
                } // <4>

        // props contains a Prop per size-bucket.
        val prop: Prop = props.map { p ->
            { max: Int, _: Int, rng: RNG ->
                p(max, casePerSize, rng)
            }
        }
            // We transform these bucket Props into a single
            // Prop by chaining them.
            .reduce { p1, p2 -> p1.and(p2) } // <5>

        // The value passed for max is ignored, and so irrelevant,
        // as passing 100 instead of the actual max here shows.
        // The 100 doesn't change the behavior.
        // Here we check the composite Prop, i.e. the Prop that
        // includes all Props in all buckets.
        // TODO Does n have any effect?
        prop(100, n, rng) // <6>
        // prop.check(max, n, rng) // <6>
    }

//tag::ignore[]
// We generate the props within each size-bucket.
// Type param A here is actually List<Int>.
fun <A> forAll(ga: Gen<A>, f: (A) -> Boolean): Prop =
    // Here n eventually gets the value of casePerSize.
    // It will be the same value for all size-buckets, since
    // we've made sure that each size-bucket will contain the
    // same number of cases.
    { _: Int, n: Int, rng: RNG ->
        randomSequence(ga, rng).mapIndexed { i, a ->

            println("a $a")

            try {
                if (f(a)) Passed else Falsified(
                    a.toString(),
                    i
                )
            } catch (e: Exception) {
                Falsified(buildMessage(a, e), i)
            }
        }.take(n)
            .find { it.isFalsified() }
            .toOption()
            .getOrElse { Passed }
    }

private fun <A> randomSequence(
    ga: Gen<A>,
    rng: RNG
): Sequence<A> =
    sequence {
        val (a: A, rng2: RNG) = ga.run(rng)
        yield(a)
        yieldAll(randomSequence(ga, rng2))
    }

private fun <A> buildMessage(a: A, e: Exception) =
    """
    |test case: $a
    |generated an exception: ${e.message}
    |stacktrace:
    |${e.stackTrace.take(10).joinToString("\n")}
""".trimMargin()
//end::ignore[]

fun Prop.and(p: Prop): Prop =
    { max, n, rng -> // <7>
        when (val prop = this(max, n, rng)) {
            is Passed -> p(max, n, rng)
            is Falsified -> prop
        }
    }
//end::init[]
