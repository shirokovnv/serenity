package core.scene.raytracing

import core.math.Vector3

interface RayIntersectable {
    fun intersectsWith(origin: Vector3, direction: Vector3): Vector3?
}