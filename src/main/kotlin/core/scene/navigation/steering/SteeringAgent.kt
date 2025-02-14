package core.scene.navigation.steering

import core.math.Matrix4
import core.math.Vector3
import core.scene.navigation.agents.NavMeshAgent
import core.scene.volumes.BoxAABB

typealias NeighboursProvider = () -> MutableList<SteeringAgent>
typealias ObstaclesProvider = () -> MutableList<BoxAABB>

interface SteeringAgent : NavMeshAgent {
    var position: Vector3
    var velocity: Vector3
    var acceleration: Vector3
    var maxSpeed: Float
    var maxForce: Float
    var orientation: Matrix4

    var perceptionDistance: Float
    var target: Vector3

    var avoidanceDistance: Float
    var avoidanceRadius: Float

    val neighbours: NeighboursProvider
    val obstacles: ObstaclesProvider

    var isStatic: Boolean
}