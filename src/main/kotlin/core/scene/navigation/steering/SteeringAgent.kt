package core.scene.navigation.steering

import core.math.Matrix4
import core.math.Vector3

typealias NeighboursProvider = () -> MutableList<SteeringAgent>

interface SteeringAgent {
    var position: Vector3
    var velocity: Vector3
    var acceleration: Vector3
    var maxSpeed: Float
    var maxForce: Float
    var orientation: Matrix4

    var perceptionDistance: Float
    var target: Vector3

    val neighbours: NeighboursProvider
}