package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.helpers.distance
import core.scene.navigation.path.Path
import core.scene.navigation.steering.SteeringAgent

class PathFollowCommand(
    val path: Path,
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE_UNLESS_SUCCEEDED.value
) : SteeringCommand() {
    override var priority: Int = 0

    private var targetIndex = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        val waypoints = path.waypoints()
        if (waypoints.size < 2) {
            return SteeringCommandResult(Vector3(0f), true)
        }

        val pathEnd = waypoints.last()
        if (distance(pathEnd, actor.position) < actor.stepSize) {
            return SteeringCommandResult(Vector3(0f), true)
        }

        val target = waypoints[targetIndex]
        val seekCommand = SeekCommand(target, actor.stepSize)
        val result = seekCommand.execute(actor)

        if (distance(target, actor.position) < actor.stepSize) {
            targetIndex++
        }

        return SteeringCommandResult(result.steeringForce, false)
    }
}