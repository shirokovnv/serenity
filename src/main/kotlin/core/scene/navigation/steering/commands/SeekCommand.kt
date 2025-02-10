package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.helpers.distance
import core.math.truncate
import core.scene.navigation.steering.SteeringAgent

class SeekCommand(
    var target: Vector3,
    val radiusOfInfluence: Float,
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE_UNLESS_SUCCEEDED.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        if (distance(actor.position, target) < radiusOfInfluence) {
            return SteeringCommandResult(Vector3(0f), true)
        }

        val desiredVelocity = (target - actor.position).normalize() * actor.maxSpeed
        val steering = (desiredVelocity - actor.velocity) * weight

        return SteeringCommandResult(
            steering.truncate(actor.maxForce),
            false
        )
    }
}