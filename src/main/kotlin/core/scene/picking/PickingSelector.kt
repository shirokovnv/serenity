package core.scene.picking

import core.math.Vector3
import core.math.helpers.distanceSquared
import core.scene.Object
import core.scene.raytracing.RayIntersectionDetector
import core.scene.volumes.BoxAABB

object PickingSelector {
    fun selectInRange(rayOrigin: Vector3, rayDirection: Vector3, rayLength: Float): MutableList<BoxAABB> {
        return PickingContainer.pickings()
            .mapNotNull { (it.owner() as Object).getComponent<BoxAABB>() }
            .parallelStream()
            .filter { bounds -> distanceSquared(rayOrigin, bounds.shape().center) <= rayLength * rayLength }
            .sorted(PickingSortComparator(rayOrigin))
            .filter { bounds ->
                RayIntersectionDetector.rayIntersects(
                    rayOrigin,
                    rayDirection,
                    bounds.shape()
                ) != null
            }
            .toList()
    }
}