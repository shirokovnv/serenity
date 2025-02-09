package core.scene.navigation

import core.scene.navigation.agents.NavMeshAgent
import core.scene.navigation.obstacles.NavMeshObstacle

abstract class NavMesh {
    protected val obstacles = mutableListOf<NavMeshObstacle>()
    protected val agents = mutableListOf<NavMeshAgent>()

    fun addObstacle(obstacle: NavMeshObstacle): Boolean {
        return obstacles.add(obstacle)
    }

    fun removeObstacle(obstacle: NavMeshObstacle): Boolean {
        return obstacles.remove(obstacle)
    }

    fun addObstacles(obstaclesCollection: List<NavMeshObstacle>) {
        obstacles.addAll(obstaclesCollection)
    }

    fun clearObstacles() {
        obstacles.clear()
    }

    fun addAgent(agent: NavMeshAgent): Boolean {
        return agents.add(agent)
    }

    fun removeAgent(agent: NavMeshAgent): Boolean {
        return agents.remove(agent)
    }

    fun addAgents(agentsCollection: List<NavMeshAgent>) {
        agents.addAll(agents)
    }

    fun clearAgents() {
        agents.clear()
    }

    abstract fun bake()
}