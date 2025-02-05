package core.scene.raytracing

import core.math.Vector3

fun getPointOnRay(rayOrigin: Vector3, rayDirection: Vector3, distance: Float) : Vector3 {
    val scaledRay = rayDirection * distance
    return rayOrigin + scaledRay
}

