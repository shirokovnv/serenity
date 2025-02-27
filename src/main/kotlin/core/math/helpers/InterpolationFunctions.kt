package core.math.helpers

import core.math.Quaternion
import core.math.Vector3
import kotlin.math.*

fun lerp(a: Float, b: Float, t: Float): Float {
    return a + t * (b - a)
}

fun lerp(a: Vector3, b: Vector3, t: Float): Vector3 {
    return Vector3(
        a.x + (b.x - a.x) * t,
        a.y + (b.y - a.y) * t,
        a.z + (b.z - a.z) * t
    )
}

fun lerp(a: Quaternion, b: Quaternion, t: Float): Quaternion {
    return Quaternion(
        a.x + (b.x - a.x) * t,
        a.y + (b.y - a.y) * t,
        a.z + (b.z - a.z) * t,
        a.w + (b.w - a.w) * t
    )
}

fun slerp(src: Quaternion, dest: Quaternion, factor: Float, shortest: Boolean = false): Quaternion {
    val epsilon = 1e-3f
    var cos = src.dot(dest)
    var correctedDest = Quaternion(dest)

    if (shortest && cos < 0) {
        cos = -cos
        correctedDest = Quaternion(-dest.x, -dest.y, -dest.z, -dest.w)
    }

    if (abs(cos) >= 1 - epsilon) {
        return nlerp(src, correctedDest, factor, shortest)
    }

    val sin = sqrt(1.0f - cos * cos)
    val angle = atan2(sin, cos)
    val invSin = 1.0f / sin

    val srcFactor = sin((1.0f - factor) * angle) * invSin
    val destFactor = sin(factor * angle) * invSin

    return (src * srcFactor) + (correctedDest * destFactor)
}

fun nlerp(src: Quaternion, dest: Quaternion, factor: Float, shortest: Boolean = false): Quaternion {
    val correctedDest = if (shortest && src.dot(dest) < 0) {
        Quaternion(-dest.x, -dest.y, -dest.z, -dest.w)
    } else {
        Quaternion(dest)
    }
    return lerp(src, correctedDest, factor)
}

fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
    val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}