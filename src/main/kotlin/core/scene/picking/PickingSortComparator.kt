package core.scene.picking

import core.math.Vector3
import core.math.helpers.distanceSquared
import core.scene.volumes.BoxAABB

class PickingSortComparator(private val origin: Vector3): Comparator<BoxAABB> {
    override fun compare(o1: BoxAABB?, o2: BoxAABB?): Int {
        val distance1 = distanceSquared(origin, o1!!.shape().center)
        val distance2 = distanceSquared(origin, o2!!.shape().center)

        return distance1.compareTo(distance2)
    }
}