package core.math

object OverlapDetector {
    fun contains(rectA: Rect2d, rectB: Rect2d): Boolean {
        return rectA.min.x <= rectB.min.x &&
                rectA.min.y <= rectB.min.y &&
                rectA.max.x >= rectB.max.x &&
                rectA.max.y >= rectB.max.y
    }

    fun contains(rectA: Rect3d, rectB: Rect3d): Boolean {
        return rectA.min.x <= rectB.min.x &&
                rectA.min.y <= rectB.min.y &&
                rectA.min.z <= rectB.min.z &&
                rectA.max.x >= rectB.max.x &&
                rectA.max.y >= rectB.max.y &&
                rectA.max.z >= rectB.max.z
    }

    fun contains(sphereA: Sphere, sphereB: Sphere): Boolean {
        val centerDelta = Vector3(
            sphereA.center.x - sphereB.center.x,
            sphereA.center.y - sphereB.center.y,
            sphereA.center.z - sphereB.center.z
        )

        val distanceSquared = centerDelta.dot(centerDelta)
        val radiusDeltaSquared = (sphereA.radius - sphereB.radius) * (sphereA.radius - sphereB.radius)

        return distanceSquared <= radiusDeltaSquared
    }

    fun contains(rect: Rect3d, sphere: Sphere): Boolean {
        if (sphere.center.x < rect.min.x || sphere.center.x > rect.max.x ||
            sphere.center.y < rect.min.y || sphere.center.y > rect.max.y ||
            sphere.center.z < rect.min.z || sphere.center.y > rect.max.z
        ) {
            return false
        }

        val sphereMinX = sphere.center.x - sphere.radius
        val sphereMaxX = sphere.center.x + sphere.radius
        val sphereMinY = sphere.center.y - sphere.radius
        val sphereMaxY = sphere.center.y + sphere.radius
        val sphereMinZ = sphere.center.z - sphere.radius
        val sphereMaxZ = sphere.center.z + sphere.radius

        return !(sphereMinX < rect.min.x || sphereMaxX > rect.max.x ||
                sphereMinY < rect.min.y || sphereMaxY > rect.max.y ||
                sphereMinZ < rect.min.z || sphereMaxZ > rect.max.z)

    }

    fun contains(sphere: Sphere, rect: Rect3d): Boolean {
        for (corner in rect.corners) {
            val dx = sphere.center.x - corner.x
            val dy = sphere.center.y - corner.y
            val dz = sphere.center.z - corner.z
            val distanceSquared = dx * dx + dy * dy + dz * dz
            if (distanceSquared > sphere.radius * sphere.radius) {
                return false
            }
        }
        return true
    }
}