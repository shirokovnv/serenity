package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Rect3d
import core.math.Vector3
import core.math.helpers.distance
import core.scene.navigation.steering.SteeringAgent
import core.scene.volumes.BoxAABB

class ObstacleAvoidanceCommand(
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        val ahead = if (actor.velocity.lengthSquared() > 0.0001f) {
            actor.position + Vector3(actor.velocity).normalize() * actor.avoidanceDistance
        } else {
            Vector3(actor.position)
        }

        val position = Vector3(actor.position.x, 0f, actor.position.z)
        val collision = actor
            .obstacles()
            .map {
                val shape = it.shape()
                val minPoint = Vector3(shape.min.x, 0f, shape.min.z)
                val maxPoint = Vector3(shape.max.x, 0f, shape.max.z)
                BoxAABB(Rect3d(minPoint, maxPoint))
            }
            .filter {
                distance(it.shape().center, position) < actor.avoidanceDistance
            }.minByOrNull {
                distance(it.shape().center, ahead) < actor.avoidanceRadius
            }

        val steering = if (collision != null) {
            val awayFrom = (ahead - collision.shape().center)
            if (awayFrom.lengthSquared() > 0.0001f) {
                val desiredVelocity = awayFrom.normalize()
                (desiredVelocity - actor.velocity) * weight
            } else {
                Vector3(0f)
            }
        } else {
            Vector3(0f)
        }

        return SteeringCommandResult(
            steering,
            true
        )
    }
}