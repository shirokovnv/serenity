package core.scene.volumes

import core.ecs.BaseComponent
import core.math.Rect3d
import core.math.Vector3
import core.scene.Transform
import kotlin.math.max
import kotlin.math.min

class BoxAABBHierarchy : BaseComponent() {
    private val innerBounds = mutableListOf<BoxAABB>()

    fun add(boxAABB: BoxAABB) {
        innerBounds.add(boxAABB)
    }

    fun remove(boxAABB: BoxAABB) {
        innerBounds.remove(boxAABB)
    }

    fun clear() {
        innerBounds.clear()
    }

    fun innerBounds(): MutableList<BoxAABB> = innerBounds.toMutableList()

    fun outerBounds(): BoxAABB {
        if (innerBounds.isEmpty()) {
            throw IllegalStateException("Inner bounds is empty.")
        }

        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY

        innerBounds.forEach { bounds ->
            minX = min(minX, bounds.shape().min.x)
            minY = min(minY, bounds.shape().min.y)
            minZ = min(minZ, bounds.shape().min.z)

            maxX = max(maxX, bounds.shape().max.x)
            maxY = max(maxY, bounds.shape().max.y)
            maxZ = max(maxZ, bounds.shape().max.z)
        }

        return BoxAABB(Rect3d(Vector3(minX, minY, minZ), Vector3(maxX, maxY, maxZ)))
    }

    fun transformedInnerBounds(): List<BoxAABB> {
        if (innerBounds.isEmpty() || owner() == null) {
            return emptyList()
        }

        val transform = owner()!!.getComponent<Transform>()!!

        return innerBounds
            .map { BoxAABB(it.shape()).transform(transform) }
            .toList()
    }
}