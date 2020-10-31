package chapter6.exercises

import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.IdFunctor
import arrow.core.extensions.IdMonad
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.mtl.State
import arrow.mtl.StateApi
import arrow.mtl.extensions.fx
import arrow.mtl.extensions.monad
import arrow.mtl.fix
import arrow.mtl.runS
import arrow.mtl.stateSequential
import arrow.mtl.stateTraverse
import chapter11.State.Companion.get
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlinx.collections.immutable.persistentListOf

//tag::init1[]
sealed class Input

object Coin : Input()
object Turn : Input()

data class Machine(
    val locked: Boolean,
    val candies: Int,
    val coins: Int
)
//end::init1[]

/**
 * TODO: Re-enable tests by removing `!` prefix!
 */
class Exercise_6_11 : WordSpec({

    //tag::init2[]
    fun simulateMachine1(
        inputs: List<Input>
    ): State<Machine, Tuple2<Int, Int>> =
        inputs.stateTraverse { input ->
            State<Machine, Tuple2<Int, Int>> { m ->
                val mm = when (input) {
                    is Coin -> when {
                        !m.locked -> m
                        m.candies > 0 -> {
                            m.copy(locked = false, coins = m.coins + 1)
                        }
                        else -> m
                    }
                    is Turn -> when {
                        m.locked -> m
                        else -> m.copy(locked = true, candies = m.candies - 1)
                    }
                }
                Tuple2(mm, Tuple2(m.candies, m.coins))
            }
        }.map(Id.functor()) { it[it.size - 1] }

    val update: (Input) -> (Machine) -> Machine =
        { i: Input ->
            { s: Machine ->
                when (i) {
                    is Coin ->
                        if (!s.locked || s.candies == 0) s
                        else Machine(false, s.candies, s.coins + 1)
                    is Turn ->
                        if (s.locked || s.candies == 0) s
                        else Machine(true, s.candies - 1, s.coins)
                }
            }
        }
    fun simulateMachine(
        inputs: List<Input>
    ): State<Machine, Tuple2<Int, Int>> =
        State.fx<ForId, Machine, Tuple2<Int, Int>>(Id.monad()) {
            val (x) = inputs.map(update)
                .map(StateApi::modify).stateSequential()
            val (s) = StateApi.get<Machine>()
            Tuple2(s.candies, s.coins)
        }
    //end::init2[]

    "simulateMachine" should {
        "allow the purchase of a single candy" {
            val actions = persistentListOf(Coin)
            val before =
                Machine(locked = true, candies = 1, coins = 0)
            val after =
                Machine(locked = false, candies = 1, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
        "allow the redemption of a single candy" {
            val actions = persistentListOf(Turn)
            val before =
                Machine(locked = false, candies = 1, coins = 1)
            val after = Machine(locked = true, candies = 0, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
        "allow purchase and redemption of a candy" {
            val actions = persistentListOf(Coin, Turn)
            val before =
                Machine(locked = true, candies = 1, coins = 0)
            val after = Machine(locked = true, candies = 0, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }

    "inserting a coin into a locked machine" should {
        "unlock the machine if there is some candy" {
            val actions = persistentListOf(Coin)
            val before =
                Machine(locked = true, candies = 1, coins = 0)
            val after =
                Machine(locked = false, candies = 1, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
    "inserting a coin into an unlocked machine" should {
        "do nothing" {
            val actions = persistentListOf(Coin)
            val before =
                Machine(locked = false, candies = 1, coins = 1)
            val after =
                Machine(locked = false, candies = 1, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
    "turning the knob on an unlocked machine" should {
        "cause it to dispense candy and lock" {
            val actions = persistentListOf(Turn)
            val before =
                Machine(locked = false, candies = 1, coins = 1)
            val after = Machine(locked = true, candies = 0, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
    "turning the knob on a locked machine" should {
        "do nothing" {
            val actions = persistentListOf(Turn)
            val before =
                Machine(locked = true, candies = 1, coins = 1)
            val after = Machine(locked = true, candies = 1, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
    "a machine that is out of candy" should {
        "ignore the turn of a knob" {
            val actions = persistentListOf(Turn)
            val before =
                Machine(locked = true, candies = 0, coins = 0)
            val after = Machine(locked = true, candies = 0, coins = 0)
            simulateMachine(actions).runS(before) shouldBe after
        }
        "ignore the insertion of a coin" {
            val actions = persistentListOf(Coin)
            val before =
                Machine(locked = true, candies = 0, coins = 0)
            val after = Machine(locked = true, candies = 0, coins = 0)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
})
