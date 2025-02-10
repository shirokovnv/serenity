package core.scene.navigation.steering.commands

import core.commands.CommandFlag
import core.math.Vector3
import core.math.helpers.distance
import core.math.helpers.pointSegmentProjection
import core.scene.navigation.path.Path
import core.scene.navigation.path.findNearestPointAndSegment
import core.scene.navigation.steering.SteeringAgent

class PathFollowCommand(
    val path: Path,
    val arrivalRadius: Float,
    val predictionTime: Float = 1.0f,
    val pathThreshold: Float = 0.01f,
    override var weight: Float = 1.0f,
    override var flags: Int = CommandFlag.REPEATABLE_UNLESS_SUCCEEDED.value
) : SteeringCommand() {
    override var priority: Int = 0

    override fun execute(actor: SteeringAgent): SteeringCommandResult {
        val waypoints = path.waypoints()
        val nearestPointAndSegment = findNearestPointAndSegment(
            actor.position,
            waypoints
        )

        if (nearestPointAndSegment != null) {
            val (_, index) = nearestPointAndSegment
            val predictedPoint = actor.position + actor.velocity * predictionTime

            val segmentStart = waypoints[index]
            val segmentEnd = waypoints[index + 1]

            val target = pointSegmentProjection(predictedPoint, segmentStart, segmentEnd)
            val distanceBetween = distance(actor.position, target)

            if (distanceBetween < pathThreshold) {
                return SteeringCommandResult(Vector3(0f), true)
            }

            // The end is near
            if (distance(actor.position, segmentEnd) <= arrivalRadius) {
                val arriveCommand = ArriveCommand(segmentEnd, arrivalRadius)
                return arriveCommand.execute(actor)
            }

            val seekCommand = SeekCommand(target, 0.01f)
            return seekCommand.execute(actor)
        }

        return SteeringCommandResult(Vector3(0f), true)
    }
}