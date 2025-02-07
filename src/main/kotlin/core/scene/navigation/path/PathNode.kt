package core.scene.navigation.path

import core.math.Vector3

class PathNode (val point: Vector3) {
    var gCost: Float = Float.MAX_VALUE
    var hCost: Float = 0f
    var fCost: Float = 0f
    var isWalkable: Boolean = true
    var prevNode: PathNode? = null

    init {
        calculateFCost()
    }

    fun calculateFCost() {
        fCost = gCost + hCost
    }

    override fun toString(): String {
        return point.toString()
    }
}