package core.scene.spatial

import core.math.IntersectionDetector
import core.math.Rect3d
import core.scene.BoundingVolume
import core.scene.Object
import java.util.*

class LinearQuadTreeNode {

    private val children: Array<LinearQuadTreeNode?> = arrayOfNulls(4)
    private var parent: LinearQuadTreeNode? = null
    private var objects = Collections.synchronizedSet(mutableSetOf<Object>())

    fun addOrUpdateMember(member: Object, rect: LinearQuadTreeRect): Boolean {
        return objects.add(member)
    }

    fun removeMember(member: Object): Boolean {
        return objects.remove(member)
    }

    fun isEmpty(): Boolean {
        return objects.isEmpty()
    }

    internal fun setup(
        parent: LinearQuadTreeNode?,
        child0: LinearQuadTreeNode?,
        child1: LinearQuadTreeNode?,
        child2: LinearQuadTreeNode?,
        child3: LinearQuadTreeNode?
    ) {
        this.parent = parent
        this.children[0] = child0
        this.children[1] = child1
        this.children[2] = child2
        this.children[3] = child3
    }

    fun getObjects(): List<Object> {
        return objects.toList()
    }

    fun findCollisions(searchRect: Rect3d): List<Object> {
        return objects.filter {obj ->
            IntersectionDetector.intersects(obj.getComponent<BoundingVolume>()!!.toRect3d(), searchRect)
        }
    }
}