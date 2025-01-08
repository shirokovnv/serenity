package core.math

object ShapeIntersector {
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
        val closestX = when {
            sphere.center.x < rect.min.x -> rect.min.x
            sphere.center.x > rect.max.x -> rect.max.x
            else -> sphere.center.x
        }

        val closestY = when {
            sphere.center.y < rect.min.y -> rect.min.y
            sphere.center.y > rect.max.y -> rect.max.y
            else -> sphere.center.y
        }

        val closestZ = when {
            sphere.center.z < rect.min.z -> rect.min.z
            sphere.center.z > rect.max.z -> rect.max.z
            else -> sphere.center.z
        }

        val distX = sphere.center.x - closestX
        val distY = sphere.center.y - closestY
        val distZ = sphere.center.z - closestZ;

        val distanceSquared = distX * distX + distY * distY + distZ * distZ;

        return distanceSquared <= sphere.radius * sphere.radius
    }
}