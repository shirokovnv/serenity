package modules.terrain.navigation

import core.scene.Object
import core.scene.navigation.agents.NavMeshAgent
import core.scene.navigation.heuristics.DiagonalDistanceHeuristic
import core.scene.navigation.heuristics.PathHeuristicInterface
import core.scene.volumes.BoxAABB

class TerrainNavMeshAgent(override val objectRef: Object) : NavMeshAgent {
    override var radius: Float = 10.0f
    override var maxSlope: Float = 0.5f
    override var stepSize: Float = 5.0f

    override fun getAgentBounds(): BoxAABB {
        return objectRef.bounds()
    }

    override fun getHeuristic(): PathHeuristicInterface {
        return DiagonalDistanceHeuristic()
    }
}