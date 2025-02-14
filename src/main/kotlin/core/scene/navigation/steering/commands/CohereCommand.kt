package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.helpers.distance
import core.scene.navigation.steering.SteeringAgent

class CohereCommand(
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        // Calculate average location
        var desiredVelocity = Vector3(0f)
        var count = 0
        actor.neighbours().forEach { neighbour ->
            val distanceBetween = distance(actor.position, neighbour.position)
            if (distanceBetween <= actor.perceptionDistance) {
                desiredVelocity = desiredVelocity + neighbour.position
                count++
            }
        }

        val steering = if (count > 0) {
            desiredVelocity = desiredVelocity / count.toFloat()
            desiredVelocity = desiredVelocity - actor.position

            if (desiredVelocity.lengthSquared() > 0.0001f) {
                desiredVelocity.normalize()
                desiredVelocity = desiredVelocity * actor.maxSpeed
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