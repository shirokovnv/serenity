package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.truncate
import core.scene.navigation.steering.SteeringAgent
import kotlin.math.min

class ArriveCommand(
    var target: Vector3,
    val radiusOfInfluence: Float,
    val distanceThreshold: Float = 0.01f,
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE_UNLESS_SUCCEEDED.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        var desiredVelocity = target - actor.position
        val distance = desiredVelocity.length()

        if (distance < distanceThreshold) {
            return SteeringCommandResult(Vector3(0f), true)
        }

        val rampedSpeed = actor.maxSpeed * (distance / radiusOfInfluence)
        val clippedSpeed = min(rampedSpeed, actor.maxSpeed)
        desiredVelocity = desiredVelocity * (clippedSpeed / distance)
        val steering = desiredVelocity - actor.velocity

        return SteeringCommandResult(
            steering.truncate(actor.maxForce),
            false
        )
    }
}