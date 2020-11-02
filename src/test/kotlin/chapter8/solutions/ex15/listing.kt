package chapter8.solutions.ex15

import arrow.core.extensions.list.foldable.foldLeft
import chapter7.sec4.Par
import chapter7.sec4.fork
import chapter7.sec4.unit
import chapter8.sec3.listing3.Gen
import chapter8.sec3.listing3.choose
import chapter8.sec3.listing3.flatMap
import chapter8.sec3.listing3.listOfN
import chapter8.sec3.listing3.map
import chapter8.sec4.listing9.map2

//tag::init[]
val pint2: Gen<Par<Int>> =
    choose(0, 20).flatMap { n ->
        listOfN(n, choose(-100, 100)).map { ls ->
            ls.foldLeft(unit(0)) { pint, i ->
                fork {
                    map2(pint, unit(i)) { a, b ->
                        a + b
                    }
                }
            }
        }
    }
//end::init[]
