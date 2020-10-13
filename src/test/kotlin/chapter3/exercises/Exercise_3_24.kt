package chapter3.exercises

import chapter3.Branch
import chapter3.Leaf
import chapter3.Tree
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

// tag::init[]
fun <A> size_nontailrec(tree: Tree<A>): Int =
    when (tree) {
        is Branch -> size(tree.left) + size(tree.right) + 1
        is Leaf -> 1
    }

fun <A> size_tailrec(tree: Tree<A>): Int {
    tailrec fun go(tree: Tree<A>, rights: List<Tree<A>>, size: Int = 0): Int =
        when (tree) {
            is Branch -> go(tree.left, rights + tree.right, size + 1)
            is Leaf -> if (rights.isNotEmpty())
                go(rights.first(), rights.drop(1), size + 1)
            else size + 1
        }
    return go(tree, emptyList())
}

fun <A> size(tree: Tree<A>) = size_tailrec(tree)
// end::init[]

class Exercise_3_24 : WordSpec({
    "tree size" should {
        "determine the total size of a tree" {
            val tree =
                Branch(
                    Branch(Leaf(1), Leaf(2)),
                    Branch(Leaf(3), Leaf(4))
                )
            size(tree) shouldBe 7
        }
    }
})
