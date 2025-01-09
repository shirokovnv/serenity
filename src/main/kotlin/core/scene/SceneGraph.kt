package core.scene

import core.math.Rect3d

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
        root.getComponent<BoxAABB>()!!.setShape(worldBounds)
    }

    fun traverse(visit: SceneObjectVisitor, traversalOrder: TraversalOrder) {
        traverse(root, visit, traversalOrder)
    }
}