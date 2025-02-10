package core.scene.navigation.steering.commands

import core.commands.CommandResultInterface
import core.math.Vector3

class SteeringCommandResult(
    val steeringForce: Vector3,
    val isSuccessful: Boolean
) : CommandResultInterface