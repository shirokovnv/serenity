package core.math.helpers

import core.math.Vector3
import core.math.extensions.clamp

fun pointSegmentProjection(point: Vector3, segmentStart: Vector3, segmentEnd: Vector3): Vector3 {
    val segmentVector = segmentEnd - segmentStart
    val segmentLengthSquared = segmentVector.lengthSquared()

    if (segmentLengthSquared == 0f) {
        // start == end
        return segmentStart
    }

    val t =
        ((point - segmentStart).x * segmentVector.x + (point - segmentStart).y * segmentVector.y + (point - segmentStart).z * segmentVector.z) / segmentLengthSquared

    return segmentStart + segmentVector * t.clamp(0f, 1f)
}

fun pointLineProjection(point: Vector3, segmentStart: Vector3, segmentEnd: Vector3): Vector3 {
    val v1 = point - segmentStart
    val v2 = segmentEnd - segmentStart

    v2.normalize()
    val dot = v1.dot(v2)
    v2 *= dot

    return v2 + segmentStart
}