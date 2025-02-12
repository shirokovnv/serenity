package core.scene.navigation.path

import core.math.Vector3
import core.math.helpers.pointSegmentProjection

fun findNearestPointAndSegment(position: Vector3, path: List<Vector3>): Pair<Vector3, Int>? {
    if (path.size < 2) {
        return null // Path must contain at least 2 points
    }

    var nearestPoint: Vector3? = null
    var nearestSegmentIndex = -1
    var minDistanceSquared = Float.MAX_VALUE

    for (i in 0..<path.size - 1) {
        val segmentStart = path[i]
        val segmentEnd = path[i + 1]

        val projection = pointSegmentProjection(position, segmentStart, segmentEnd)
        val distanceSquared = (position - projection).lengthSquared()

        if (distanceSquared < minDistanceSquared) {
            minDistanceSquared = distanceSquared
            nearestPoint = projection
            nearestSegmentIndex = i
        }
    }

    return if (nearestPoint != null) {
        Pair(nearestPoint, nearestSegmentIndex)
    } else {
        null
    }
}