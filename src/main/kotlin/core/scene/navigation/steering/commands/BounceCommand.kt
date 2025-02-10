package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.truncate
import core.scene.navigation.NavigatorInterface
import core.scene.navigation.steering.SteeringAgent

class BounceCommand(
    val navigator: NavigatorInterface,
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        if (actor.velocity.lengthSquared() < 0.01f) {
            return SteeringCommandResult(Vector3(0f), true)
        }

        val ahead = actor.position + actor.velocity.normalize() * actor.avoidanceRadius
        val desiredVelocity = if (navigator.outOfBounds(ahead, actor)) {
            (actor.position - ahead).normalize() * actor.maxSpeed
        } else {
            actor.velocity
        }

        val steering = desiredVelocity - actor.velocity

        return SteeringCommandResult(
            steering.truncate(actor.maxForce),
            true
        )
    }
}