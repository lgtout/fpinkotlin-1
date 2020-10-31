package chapter8.practice

interface RNG {
    fun nextInt(): Pair<Int, RNG>
}

data class SimpleRNG(val seed: Long) : RNG {
    override fun nextInt(): Pair<Int, RNG> {
        // 0x5DEECE66DL =>    25214903917     => 10111011110111011001110011001101101
        // 0xBL =>                     11     => 1011
        // 0xFFFFFFFFFFFFL => 281474976710655 => 111111111111111111111111111111111111111111111111 (48 1s)
        val newSeed = (seed * 0x5DEECE66DL + 0xBL) and 0xFFFFFFFFFFFFL
        val nextRNG = SimpleRNG(newSeed)
        val n = (newSeed ushr 16).toInt()
        return Pair(n, nextRNG)
    }
}

fun nonNegativeInt(rng: RNG): Pair<Int, RNG> {
    val (i1, rng2) = rng.nextInt()
    return Pair(if (i1 < 0) -(i1 + 1) else i1, rng2)
}

fun nextBoolean(rng: RNG): Pair<Boolean, RNG> {
    val (i1, rng2) = rng.nextInt()
    return Pair(i1 >= 0, rng2)
}

fun double(rng: RNG): Pair<Double, RNG> {
    val (i, rng2) = nonNegativeInt(rng)
    return Pair(i / (Int.MAX_VALUE.toDouble() + 1), rng2)
}

data class State<S, out A>(val run: (S) -> Pair<A, S>) {

    companion object {
        fun <S, A> unit(a: A): State<S, A> =
            State { s: S -> Pair(a, s) }

        fun <S, A, B, C> map2(
            ra: State<S, A>,
            rb: State<S, B>,
            f: (A, B) -> C
        ): State<S, C> =
            ra.flatMap { a ->
                rb.map { b ->
                    f(a, b)
                }
            }

        fun <S, A> sequence(
            fs: List<State<S, A>>
        ): State<S, List<A>> =
            fs.foldRight(unit(emptyList())) { f, acc ->
                map2(f, acc) { h, t -> listOf(h) + t }
            }
    }

    fun <B> map(f: (A) -> B): State<S, B> =
        flatMap { a -> unit<S, B>(f(a)) }

    fun <B> flatMap(f: (A) -> State<S, B>): State<S, B> =
        State { s: S ->
            val (a: A, s2: S) = this.run(s)
            f(a).run(s2)
        }
}
