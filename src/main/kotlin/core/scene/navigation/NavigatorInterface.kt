package core.scene.navigation

import core.math.Vector3
import core.scene.navigation.agents.NavMeshAgent
import core.scene.navigation.path.PathNode
import core.scene.navigation.path.PathResult

interface NavigatorInterface {
    fun calculatePath(start: Vector3, finish: Vector3, agent: NavMeshAgent): PathResult
    fun evaluatePoint(point: Vector3, agent: NavMeshAgent): Boolean
    fun evaluateNode(point: Vector3, agent: NavMeshAgent): PathNode?
    fun outOfBounds(point: Vector3, agent: NavMeshAgent): Boolean
}