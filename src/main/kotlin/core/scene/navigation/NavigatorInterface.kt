package core.scene.navigation

import core.math.Vector3
import core.scene.navigation.agents.NavMeshAgent
import core.scene.navigation.path.Path

interface NavigatorInterface {
    fun calculatePath(start: Vector3, finish: Vector3, agent: NavMeshAgent): Path
    fun evaluatePoint(point: Vector3, agent: NavMeshAgent): Boolean
    fun outOfBounds(point: Vector3, agent: NavMeshAgent): Boolean
}