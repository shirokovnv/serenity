package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.scene.navigation.NavigatorInterface
import core.scene.navigation.steering.SteeringAgent

class BounceCommand(
    val navigator: NavigatorInterface,
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        if (actor.velocity.lengthSquared() < 0.0001f) {
            return SteeringCommandResult(Vector3(0f), true)
        }

        val ahead = actor.position + Vector3(actor.velocity).normalize() * actor.avoidanceDistance
        val steering = if (navigator.outOfBounds(ahead, actor)) {
            val desiredVelocity = (actor.position - ahead).normalize() * actor.maxSpeed
            (desiredVelocity - actor.velocity) * weight
        } else {
            Vector3(0f)
        }

        return SteeringCommandResult(
            steering,
            true
        )
    }
}