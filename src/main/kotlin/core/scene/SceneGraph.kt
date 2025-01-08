package core.scene

import core.math.Rect3d
import java.util.*

typealias SceneObjectVisitor = (Object) -> Unit

enum class TraversalType {
    BREADTH_FIRST,
    DEPTH_FIRST
}

class SceneGraph(worldBounds: Rect3d) {
    private var root: Object = Object()

    init {
        newWorldBounds(worldBounds)
    }

    fun attachToRoot(sceneObject: Object) {
        root.addChild(sceneObject)
    }

    fun detachFromRoot(sceneObject: Object) {
        if (sceneObject.parent() == root) {
            sceneObject.setParent(null)
        }
    }

    fun newWorldBounds(worldBounds: Rect3d) {
        root.getComponent<BoundingVolume>()!!.setShape(worldBounds)
    }

    fun traverse(visit: SceneObjectVisitor, traversalType: TraversalType) {
        when(traversalType) {
            TraversalType.BREADTH_FIRST -> depthFirstTraversal(root, visit)
            TraversalType.DEPTH_FIRST -> breadthFirstTraversal(root, visit)
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
}