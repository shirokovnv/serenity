package core.scene.navigation.agents

import core.scene.Object
import core.scene.navigation.heuristics.PathHeuristicInterface
import core.scene.volumes.BoxAABB

interface NavMeshAgent {
    var radius: Float
    var maxSlope: Float
    var stepSize: Float
    val objectRef: Object

    fun getAgentBounds(): BoxAABB
    fun getHeuristic(): PathHeuristicInterface
}