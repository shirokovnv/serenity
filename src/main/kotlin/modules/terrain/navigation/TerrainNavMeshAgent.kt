package modules.terrain.navigation

import core.math.Vector3
import core.scene.navigation.agents.BaseNavMeshAgent
import core.scene.navigation.heuristics.DiagonalDistanceHeuristic
import core.scene.navigation.heuristics.PathHeuristicInterface

class TerrainNavMeshAgent(
    initialWorldPosition: Vector3,
) : BaseNavMeshAgent(initialWorldPosition) {
    override val pathHeuristic: PathHeuristicInterface
        get() = DiagonalDistanceHeuristic()

    override var radius: Float = 10.0f
    override var maxSlope: Float = 0.5f
    override var stepSize: Float = 5.0f
}