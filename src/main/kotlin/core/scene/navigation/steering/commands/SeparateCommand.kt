package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.helpers.distance
import core.math.truncate
import core.scene.navigation.steering.SteeringAgent

class SeparateCommand(
    val separationRadius: Float,
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        var desiredVelocity = Vector3(0f)

        var count = 0
        actor.neighbours().forEach { neighbour ->
            val distanceBetween = distance(actor.position, neighbour.position)

            if (distanceBetween > 0.0001f && distanceBetween < separationRadius) {
                // Calculate vector pointing away from neighbour
                // Weight by distance
                val diff = (actor.position - neighbour.position).normalize() / distanceBetween
                desiredVelocity = desiredVelocity + diff
                count++
            }
        }

        // Average and divide by how many
        val steering = if (count > 0) {
            desiredVelocity = desiredVelocity / count.toFloat()
            desiredVelocity.normalize()
            desiredVelocity = desiredVelocity * actor.maxSpeed
            desiredVelocity - actor.velocity
        } else {
            Vector3(0f)
        }

        return SteeringCommandResult(
            steering.truncate(actor.maxForce),
            true
        )
    }
}