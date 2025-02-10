package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.helpers.distance
import core.math.truncate
import core.scene.navigation.steering.SteeringAgent

class AlignCommand(
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE.value
    ) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        val neighbours = actor.neighbours()
            .filter { neighbour -> distance(actor.position, neighbour.position) <= actor.perceptionDistance }

        val countNeighbours = neighbours.size
        var desiredVelocity = neighbours.fold(Vector3(0f, 0f, 0f)) { acc, agent ->
            acc + agent.velocity
        }

        val steering = if (countNeighbours > 0) {
            desiredVelocity = desiredVelocity / countNeighbours.toFloat()
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