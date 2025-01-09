package core.math

import kotlin.math.max
import kotlin.math.min

object IntersectionDetector {
    fun intersects(rectA: Rect2d, rectB: Rect2d): Boolean {
        return !(rectA.min.x > rectB.max.x
                || rectA.max.x < rectB.min.x
                || rectA.min.y > rectB.max.y
                || rectA.max.y < rectB.min.y)
    }

    fun intersects(rectA: Rect3d, rectB: Rect3d): Boolean {
        return !(rectA.min.x > rectB.max.x
                || rectA.max.x < rectB.min.x
                || rectA.min.y > rectB.max.y
                || rectA.max.y < rectB.min.y
                || rectA.min.z > rectB.max.z
                || rectA.max.z < rectB.min.z
                )
    }

    fun intersects(sphereA: Sphere, sphereB: Sphere): Boolean {
        val centerDelta = Vector3(
            sphereA.center.x - sphereB.center.x,
            sphereA.center.y - sphereB.center.y,
            sphereA.center.z - sphereB.center.z
        )

        val distanceSquared = centerDelta.dot(centerDelta)

        return distanceSquared <= (sphereA.radius + sphereB.radius) * (sphereA.radius + sphereB.radius)
    }

    fun intersects(rect: Rect3d, sphere: Sphere): Boolean {
        val closestX = max(rect.min.x, min(sphere.center.x, rect.max.x))
        val closestY = max(rect.min.y, min(sphere.center.y, rect.max.y))
        val closestZ = max(rect.min.z, min(sphere.center.z, rect.max.z))

        val distX = sphere.center.x - closestX
        val distY = sphere.center.y - closestY
        val distZ = sphere.center.z - closestZ

        val distanceSquared = distX * distX + distY * distY + distZ * distZ

        return distanceSquared <= sphere.radius * sphere.radius
    }
}