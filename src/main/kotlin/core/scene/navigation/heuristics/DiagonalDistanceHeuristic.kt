package core.scene.navigation.heuristics

import core.scene.navigation.path.PathNode
import kotlin.math.abs
import kotlin.math.min

class DiagonalDistanceHeuristic: PathHeuristicInterface {
    companion object {
        private const val MOVE_STRAIGHT_COST = 10f
        private const val MOVE_DIAGONAL_COST = 14f
    }

    override fun calculateDistanceCost(a: PathNode, b: PathNode): Float {
        val xDistance = abs(a.point.x - b.point.x)
        val zDistance = abs(a.point.z - b.point.z)
        val remaining = abs(xDistance - zDistance)
        return MOVE_DIAGONAL_COST * min(xDistance, zDistance) + MOVE_STRAIGHT_COST * remaining
    }
}