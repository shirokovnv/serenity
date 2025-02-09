package core.scene.navigation

import core.math.Vector3
import core.scene.navigation.agents.NavMeshAgent

data class NavRequest(
    val start: Vector3,
    val finish: Vector3,
    val agent: NavMeshAgent,
    val callback: NavResponseCallback)