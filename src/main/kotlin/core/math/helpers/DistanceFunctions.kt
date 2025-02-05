package core.math.helpers

import core.math.Vector3
import kotlin.math.sqrt

fun distance(a: Vector3, b: Vector3): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    val dz = a.z - b.z
    return sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
}

fun distanceSquared(a: Vector3, b: Vector3): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    val dz = a.z - b.z
    return dx * dx + dy * dy + dz * dz
}