package core.scene.spatial

import core.math.IntersectionDetector
import core.math.OverlapDetector
import core.math.Rect2d
import core.math.Vector2
import core.scene.BoundingVolume
import core.scene.Object
import java.util.*

class PointerQuadTree(
    private val bucketCapacity: Int,
    private val maxDepth: Int,
    private val bounds: Rect2d,
    private val level: Int = 0,
    private val allowOutOfBounds: Boolean = false
) : SpatialPartitioningInterface {

    private val objects = Collections.synchronizedSet(mutableSetOf<Object>())

    private var topLeft: PointerQuadTree? = null
    private var topRight: PointerQuadTree? = null
    private var bottomLeft: PointerQuadTree? = null
    private var bottomRight: PointerQuadTree? = null

    override fun insert(obj: Object): Boolean {
        val objectBounds = obj.getComponent<BoundingVolume>()!!.toRect2d()

        if (! (OverlapDetector.contains(bounds, objectBounds) || allowOutOfBounds)) {
            throw RuntimeException("Element is outside bounds.")
        }

        // A node exceeding its allotted number of items will get split (if it hasn't been already) into four equal quadrants.
        if (objects.size >= bucketCapacity) {
            split()
        }

        val containingChild = getContainingChild(objectBounds)

        return containingChild?.insert(obj)
            ?: // If no child was returned, then this is either a leaf node, or the element's boundaries overlap multiple children.
            // Either way, the element gets assigned to this node.
            objects.add(obj)
    }

    override fun remove(obj: Object): Boolean {
        val containingChild = getContainingChild(obj.getComponent<BoundingVolume>()!!.toRect2d())

        val removed = containingChild?.remove(obj) ?: objects.remove(obj)

        // If the total descendant element count is less than the bucket capacity, we ensure the node is in a non-split state.
        if (removed && countObjects() <= bucketCapacity) {
            merge()
        }

        return removed
    }

    override fun countObjects(): Int {
        var count = objects.size

        if (!isLeaf()) {
            count += topLeft?.countObjects() ?: 0
            count += topRight?.countObjects() ?: 0
            count += bottomLeft?.countObjects() ?: 0
            count += bottomRight?.countObjects() ?: 0
        }

        return count
    }

    override fun buildSearchResults(searchVolume: BoundingVolume): List<Object> {
        val queue = LinkedList<PointerQuadTree>()
        val searchResults = mutableListOf<Object>()
        val searchRect = searchVolume.toRect2d()

        queue.add(this)

        while (queue.isNotEmpty()) {
            val node = queue.pop()

            val nodeRect = BoundingVolume(node.bounds).toRect2d()

            if (!IntersectionDetector.intersects(nodeRect, searchVolume.toRect2d())) {
                continue
            }

            searchResults.addAll( node.objects.filter { obj ->
                IntersectionDetector.intersects(obj.getComponent<BoundingVolume>()!!.toRect2d(), searchRect)
            })

            if (!node.isLeaf()) {
                if (IntersectionDetector.intersects(searchRect, node.topLeft?.bounds!!)) {
                    queue.add(node.topLeft!!)
                }

                if (IntersectionDetector.intersects(searchRect, node.topRight?.bounds!!)) {
                    queue.add(node.topRight!!)
                }

                if (IntersectionDetector.intersects(searchRect, node.bottomLeft?.bounds!!)) {
                    queue.add(node.bottomLeft!!)
                }

                if (IntersectionDetector.intersects(searchRect, node.bottomRight?.bounds!!)) {
                    queue.add(node.bottomRight!!)
                }
            }
        }

        return searchResults
    }

    private fun isLeaf(): Boolean {
        return topLeft == null || topRight == null || bottomLeft == null || bottomRight == null
    }

    private fun split() {
        // If we're not a leaf node, then we're already split.
        if (!isLeaf()) {
            return
        }

        // Splitting is only allowed if it doesn't cause us to exceed our maximum depth.
        if (level + 1 > maxDepth) {
            return
        }

        topLeft = createChild(bounds.min)
        topRight = createChild(Vector2(bounds.min.x + bounds.width / 2, bounds.min.y))
        bottomLeft = createChild(Vector2(bounds.min.x, bounds.min.y + bounds.height / 2))
        bottomRight = createChild(Vector2(bounds.min.x + bounds.width / 2, bounds.min.y + bounds.height / 2))

        val objectList = objects.toList()

        objectList.forEach { obj ->
            val objectBounds = obj.getComponent<BoundingVolume>()!!.toRect2d()
            val containingChild = getContainingChild(objectBounds)

            if (containingChild != null) {
                objects.remove(obj)
                containingChild.insert(obj)
            }
        }
    }

    private fun merge() {
        if (isLeaf()) {
            return
        }

        topLeft?.objects?.let { objects.addAll(it) }
        topRight?.objects?.let { objects.addAll(it) }
        bottomLeft?.objects?.let { objects.addAll(it) }
        bottomRight?.objects?.let { objects.addAll(it) }

        topLeft = null
        topRight = null
        bottomLeft = null
        bottomRight = null
    }

    private fun createChild(minPoint: Vector2): PointerQuadTree {
        val maxPoint = Vector2(
            minPoint.x + bounds.width / 2,
            minPoint.y + bounds.height / 2
        )
        val newBounds = Rect2d(minPoint, maxPoint)

        return PointerQuadTree(bucketCapacity, maxDepth, newBounds, level + 1, allowOutOfBounds)
    }

    private fun getContainingChild(objectBounds: Rect2d): PointerQuadTree? {
        if (isLeaf()) {
            return null
        }

        if (OverlapDetector.contains(topLeft?.bounds!!, objectBounds)) {
            return topLeft
        }

        if (OverlapDetector.contains(topRight?.bounds!!, objectBounds)) {
            return topRight
        }

        if (OverlapDetector.contains(bottomLeft?.bounds!!, objectBounds)) {
            return bottomLeft
        }

        if (OverlapDetector.contains(bottomRight?.bounds!!, objectBounds)) {
            return bottomRight
        }

        return null
    }
}