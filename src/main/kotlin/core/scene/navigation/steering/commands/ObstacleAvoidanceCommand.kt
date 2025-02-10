package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.helpers.distance
import core.math.truncate
import core.scene.navigation.steering.SteeringAgent
import kotlin.math.sqrt

class ObstacleAvoidanceCommand(
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        if (actor.velocity.lengthSquared() < 0.01f) {
            return SteeringCommandResult(Vector3(0f), true)
        }

        val ahead = actor.position + actor.velocity.normalize() * actor.avoidanceDistance
        var desiredVelocity = Vector3(0f)
        var collisionDetected = false

        for (obstacle in actor.obstacles()) {
            val shape = obstacle.shape()
            val distance = distance(ahead, shape.center)

            if (distance < actor.avoidanceRadius + shape.size().length() / sqrt(2.0f)) {
                val awayFrom = (ahead - shape.center).normalize()
                desiredVelocity = awayFrom * actor.maxSpeed
                collisionDetected = true
                break
            }
        }

        val steering = if (collisionDetected) {
            (desiredVelocity - actor.velocity) * weight
        } else {
            Vector3(0f)
        }

        return SteeringCommandResult(
            steering.truncate(actor.maxForce),
            true
        )
    }
}