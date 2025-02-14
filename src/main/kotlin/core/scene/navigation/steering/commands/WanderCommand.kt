package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.extensions.toRadians
import core.scene.navigation.steering.SteeringAgent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class WanderCommand(
    var wanderingRadius: Float,
    var wanderingDistance: Float,
    var wanderingTheta: Float = 0f,
    var wanderingOffsetInDegrees: Float = 30.0f,
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        if (actor.velocity.lengthSquared() < 0.0001f) {
            actor.velocity = randomVelocity()
        }

        val forward = actor.velocity.normalize()

        val wanderPointCenter = actor.position + (forward * wanderingDistance)
        val x = wanderingRadius * cos(wanderingTheta)
        val z = wanderingRadius * sin(wanderingTheta)

        val wanderPoint = wanderPointCenter + Vector3(x, 0f, z)
        val steering = wanderPoint - actor.position

        wanderingTheta += randomRadianOffset(wanderingOffsetInDegrees)

        return SteeringCommandResult(
            steering,
            true
        )
    }

    private fun randomVelocity(): Vector3 {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val x = cos(angle)
        val z = sin(angle)

        return Vector3(x, 0f, z)
    }

    private fun randomRadianOffset(maxDegrees: Float): Float {
        val maxRadians = maxDegrees.toRadians()
        val randomValue = Random.nextFloat() * 2 - 1
        return randomValue * maxRadians
    }
}