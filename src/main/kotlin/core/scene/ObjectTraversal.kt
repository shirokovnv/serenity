package core.scene

import java.util.*

typealias SceneObjectVisitor = (Object) -> Unit

enum class TraversalOrder {
    BREADTH_FIRST,
    DEPTH_FIRST
}

fun traverse(node: Object, visit: SceneObjectVisitor, traversalOrder: TraversalOrder) {
    when(traversalOrder) {
        TraversalOrder.BREADTH_FIRST -> breadthFirstTraversal(node, visit)
        TraversalOrder.DEPTH_FIRST -> depthFirstTraversal(node, visit)
    }
}

private fun breadthFirstTraversal(root: Object, visit: SceneObjectVisitor) {
    val queue: Queue<Object> = LinkedList()
    queue.add(root)
    while (queue.isNotEmpty()) {
        val node = queue.remove()
        visit(node)
        for (child in node.getChildren()) {
            queue.add(child)
        }
    }
}

private fun depthFirstTraversal(node: Object, visit: SceneObjectVisitor) {
    visit(node)
    for (child in node.getChildren()) {
        depthFirstTraversal(child, visit)
    }
}