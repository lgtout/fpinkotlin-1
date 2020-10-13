package chapter7.exercises.ex3

import chapter7.sec3.Par
import chapter7.sec3.Pars
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

data class TimedMap2Future<A, B, C>(
    val pa: Future<A>,
    val pb: Future<B>,
    val f: (A, B) -> C
) : CompletableFuture<C>() {

    override fun isDone(): Boolean =
        pa.isDone && pb.isDone || isCancelled

    override fun get(): C {
        return f(pa.get(), pb.get())
    }

    override fun get(to: Long, tu: TimeUnit): C =
        f(pa.get(to, tu), pb.get(to, tu))

    override fun cancel(b: Boolean): Boolean =
        pa.cancel(b) && pb.cancel(b)

    override fun isCancelled(): Boolean =
        pa.isCancelled || pb.isCancelled
}

class Exercise_7_3 : WordSpec({

    fun <A, B, C> map2(
        a: Par<A>,
        b: Par<B>,
        f: (A, B) -> C
    ): Par<C> = { es: ExecutorService ->
        // This causes a deadlock, where a(es) is waiting to submit a task,
        //  but a(es) itself is executing within a task.  Since the thread
        //  pool is limited to a single thread, and thus a single task,
        //  progress is blocked.
        // es.submit(Callable<C> { TimedMap2Future(a(es), b(es), f).get() })
        TimedMap2Future(a(es), b(es), f)
    }

    val es: ExecutorService =
        ThreadPoolExecutor(
            1, 1, 5, TimeUnit.SECONDS, LinkedBlockingQueue()
        )

    "map2" should {
        "allow two futures to run within a given timeout" {

            val pa = Pars.fork {
                Thread.sleep(600L)
                Pars.unit(1)
            }
            val pb = Pars.fork {
                Thread.sleep(600L)
                Pars.unit("1")
            }
            val pc: Par<Long> =
                map2(pa, pb) { a: Int, b: String ->
                    a + b.toLong()
                }

            withContext(Dispatchers.IO) {
                pc(es).get(1, TimeUnit.SECONDS) shouldBe 2L
            }
        }

        "timeout if first future exceeds timeout" {

            val pa = Pars.fork {
                Thread.sleep(1100L)
                Pars.unit(1)
            }
            val pb = Pars.fork {
                Thread.sleep(500L)
                Pars.unit("1")
            }
            val pc: Par<Long> =
                map2(pa, pb) { a: Int, b: String ->
                    a + b.toLong()
                }

            withContext(Dispatchers.IO) {
                shouldThrow<TimeoutException> {
                    pc(es).get(1, TimeUnit.SECONDS)
                }
            }
        }

        "timeout if second future exceeds timeout" {

            val pa = Pars.fork {
                Thread.sleep(100L)
                Pars.unit(1)
            }
            val pb = Pars.fork {
                Thread.sleep(1000L)
                Pars.unit("1")
            }
            val pc: Par<Long> =
                map2(pa, pb) { a: Int, b: String ->
                    a + b.toLong()
                }

            withContext(Dispatchers.IO) {
                shouldThrow<TimeoutException> {
                    pc(es).get(1, TimeUnit.SECONDS)
                }
            }
        }
    }
})
