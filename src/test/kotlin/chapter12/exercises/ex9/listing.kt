package chapter12.exercises.ex9

import chapter12.Applicative
import chapter12.CompositePartialOf

//tag::init1[]
fun <F, G> compose(
    AF: Applicative<F>,
    AG: Applicative<G>
): Applicative<CompositePartialOf<F, G>> = TODO()
//end::init1[]
