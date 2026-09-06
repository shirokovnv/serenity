package core.math.helpers

import core.math.Vector2
import core.math.Vector3
import kotlin.math.*

fun pointOnCubeToSphere(p: Vector3): Vector3 {
    val x2 = p.x * p.x
    val y2 = p.y * p.y
    val z2 = p.z * p.z

    val x = p.x * sqrt(1f - (y2 + z2) / 2f + (y2 * z2) / 3f)
    val y = p.y * sqrt(1f - (z2 + x2) / 2f + (z2 * x2) / 3f)
    val z = p.z * sqrt(1f - (x2 + y2) / 2f + (x2 * y2) / 3f)

    return Vector3(x, y, z)
}

// Calculate latitude and longitude (in radians) from point on unit sphere
fun pointToCoordinate(pointOnUnitSphere: Vector3): Vector2 {
    val latitude = asin(pointOnUnitSphere.y)
    val longitude = atan2(pointOnUnitSphere.x, -pointOnUnitSphere.z)

    return Vector2(latitude, longitude)
}

// Calculate point on unit sphere given latitude and longitude (in radians)
fun coordinateToPoint(coordinate: Vector2): Vector3 {
    val y = sin(coordinate.x)
    val r = cos(coordinate.x)
    val x = r * sin(coordinate.y)
    val z = -r * cos(coordinate.y)

    return Vector3(x, y, z)
}

fun pointOnSphereToUV(p: Vector3): Vector2 {
    val pNorm = p.normalize()

    val latitude = asin(p.y)
    val longitude = atan2(p.x, -p.z)

    // Convert latitude and longitude to [0..1]
    val u = (longitude / PI.toFloat() + 1) / 2
    val v = latitude / PI.toFloat() + 0.5f

    return Vector2(u, v)
}